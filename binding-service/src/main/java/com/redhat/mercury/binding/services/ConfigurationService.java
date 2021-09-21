package com.redhat.mercury.binding.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.Header;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.redhat.mercury.binding.model.Binding;
import com.redhat.mercury.binding.model.BindingDefinition;
import com.redhat.mercury.binding.model.k8s.ServiceDomainBinding;
import com.redhat.mercury.poc.BianCloudEventConstants;

import io.cloudevents.v1.proto.CloudEvent;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.quarkus.runtime.annotations.RegisterForReflection;

@ApplicationScoped
@RegisterForReflection
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    private static final String KAFKA_ENDPOINT = "kafka:%s?brokers=%s";
    private static final String GRPC_ENDPOINT = "grpc://%s/org.bian.protobuf.InternalBindingService?method=%s";

    private static final String LABEL_SERVICE_DOMAIN = "service-domain";
    private static final String LABEL_SERVICE_TYPE = "mercury-binding";
    private static final String INTERNAL_SERVICE_TYPE = "internal";

    @Inject
    KubernetesClient kClient;

    @ConfigProperty(name = "mercury.servicedomain")
    String serviceDomainName;

    private Map<String, Binding> bindings;

    public String getBinding(CloudEvent cloudEventType, @Header("CamelGrpcMethodName") String method) {
        String ref = cloudEventType.getType().replace(BianCloudEventConstants.CE_TYPE_PREFIX, "");
        Binding binding = reduceBinding(ref, method);
        if (binding != null) {
            return binding.getEndpoint();
        }
        throw new IllegalStateException("Unable to calculate a valid QueryRoute");
    }

    private Binding reduceBinding(String type, String method) {
        if (type.isBlank()) {
            return null;
        }
        Binding binding = bindings.get(String.join(".", type, method));
        if (binding != null) {
            return binding;
        }
        return reduceBinding(type.substring(0, type.lastIndexOf(".")), method);
    }

    @PostConstruct
    void registerWatcher() {
        List<ServiceDomainBinding> bindings = kClient.resources(ServiceDomainBinding.class)
                .inNamespace(kClient.getNamespace())
                .withLabel(LABEL_SERVICE_DOMAIN, serviceDomainName)
                .list()
                .getItems();
        if (!bindings.isEmpty()) {
            updateBindings(bindings.get(0));
        }

        kClient.resources(ServiceDomainBinding.class)
                .inNamespace(kClient.getNamespace())
                .withLabel(LABEL_SERVICE_DOMAIN, serviceDomainName)
                .watch(new Watcher<>() {
                    @Override
                    public void eventReceived(Action action, ServiceDomainBinding resource) {
                        switch (action) {
                            case ADDED:
                            case MODIFIED:
                                updateBindings(resource);
                                break;
                            case DELETED:
                                clearBindings();
                                break;
                            default:
                                LOGGER.warn("Unexpected event while watching serviceDomainBindings {}", serviceDomainName, action);
                        }
                    }

                    @Override
                    public void onClose(WatcherException cause) {
                    }
                });

    }

    private synchronized void updateBindings(ServiceDomainBinding bindingConfig) {
        Builder<String, Binding> configBuilder = ImmutableMap.builder();
        if (bindingConfig == null || bindingConfig.getSpec() == null || bindingConfig.getSpec().getBindings() == null) {
            LOGGER.info("Empty bindingConfig received, clearing bindings");
            clearBindings();
            return;
        }
        bindingConfig.getSpec().getBindings().forEach(b -> {
            BindingDefinition def = new BindingDefinition().setDomainName(b.getServiceDomain())
                    .setScopeRef(b.getScopeRef()).setAction(parseAction(b.getAction()));
            Binding binding = new Binding().setDefinition(def).setEndpoint(getEndpoint(def));
            if (binding != null || binding.getEndpoint() == null) {
                configBuilder.put(String.join(".", def.getScopeRef(), def.getAction().name()), binding);
                LOGGER.info("Added binding {}", binding);
            } else {
                LOGGER.warn("Ignoring incorrect binding {}", binding);
            }
        });
        ConfigurationService.this.bindings = configBuilder.build();
        LOGGER.info("Registered all bindings");
    }

    private synchronized void clearBindings() {
        LOGGER.info("Removing any existing binding");
        ConfigurationService.this.bindings = ImmutableMap.of();
    }

    private BindingDefinition.Action parseAction(String action) {
        switch (action) {
            case "query":
                return BindingDefinition.Action.query;
            case "command":
                return BindingDefinition.Action.command;
            case "notify":
                return BindingDefinition.Action.notify;
            //TODO: subscriptions
            default:
                return null;
        }
    }

    private String getEndpoint(BindingDefinition definition) {
        switch (definition.getAction()) {
            case query:
            case command:
                return String.format(GRPC_ENDPOINT,
                        getInternalBindingService(definition.getDomainName()),
                        definition.getAction().name());
            case notify:
                return String.format(KAFKA_ENDPOINT, definition.getScopeRef(), "mercury-broker:9092");
            default:
                LOGGER.warn("Ignoring unsupported binding action: {}", definition.getAction());
                return null;
        }
    }

    private String getInternalBindingService(String serviceDomainName) {
        //TODO: Support multiple namespaces
        Map<String, String> expectedLabels = Map.of(LABEL_SERVICE_DOMAIN, serviceDomainName, LABEL_SERVICE_TYPE, INTERNAL_SERVICE_TYPE);
        List<Service> services = kClient.services().inNamespace(kClient.getNamespace()).withLabels(expectedLabels).list().getItems();
        if (services == null || services.isEmpty()) {
            return null;
        }
        if (services.size() > 1) {
            LOGGER.error("Multiple services retrieved, expected 1. Got: {}", services.size());
            return null;
        }
        Service service = services.get(0);
        String name = service.getMetadata().getName();
        Optional<ServicePort> port = service.getSpec().getPorts().stream().filter(p -> p.getName().equals(INTERNAL_SERVICE_TYPE)).findFirst();
        if (port.isPresent()) {
            return name + ":" + port.get().getPort();
        }
        LOGGER.error("Missing expected port with name {} in service {}", INTERNAL_SERVICE_TYPE, service.getMetadata().getName());
        return null;
    }
}
