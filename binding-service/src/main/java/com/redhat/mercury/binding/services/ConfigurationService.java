package com.redhat.mercury.binding.services;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Header;

import com.redhat.mercury.binding.model.Binding;
import com.redhat.mercury.binding.model.BindingDefinition;
import com.redhat.mercury.binding.model.BindingDefinition.BindingType;
import com.redhat.mercury.binding.model.ObjectRef;
import com.redhat.mercury.poc.BianCloudEventConstants;

import io.cloudevents.v1.proto.CloudEvent;

@ApplicationScoped
public class ConfigurationService {

    private static final String KAFKA_PROTOCOL = "kafka:";
    private static final String GRPC_PROTOCOL = "grpc://";
    private static final String TOPIC_NAME = "topic";

    private static final Map<String, Binding> bindings;

    // TODO: Watch configmap for real time configuration
    static {
        BindingDefinition queryCoToCcr = new BindingDefinition()
                .setType(BindingType.QUERY)
                .setScopeRef("customercreditrating.state");

        BindingDefinition commandCoToCcr = new BindingDefinition()
                .setType(BindingType.COMMAND)
                .setScopeRef("customercreditrating");
        bindings = Map.of(
                queryCoToCcr.getScopeRef(),
                new Binding()
                        .setDefinition(queryCoToCcr)
                        .setObjectRef(new ObjectRef().setName("localhost:11101")),
                commandCoToCcr.getScopeRef(),
                new Binding()
                        .setDefinition(commandCoToCcr)
                        .setObjectRef(new ObjectRef().setName("localhost:11101")));
    }

    public String getBinding(CloudEvent cloudEventType, @Header("CamelGrpcMethodName") String method) {
        Binding binding = reduceBinding(cloudEventType.getType().replace(BianCloudEventConstants.CE_TYPE_PREFIX, ""));
        if (binding != null) {
            String targetService = binding.getObjectRef().getName();
            if (method != "notify") {
                return GRPC_PROTOCOL + targetService + "/org.bian.protobuf.InternalBindingService?method=" + method;
            }
            //            return KAFKA_PROTOCOL + binding.getDefinition().getConfig().get(TOPIC_NAME) + "?brokers=" + targetService;
//            return KAFKA_PROTOCOL + "sample-topic" + "?brokers=" + targetService;
        }
        throw new IllegalStateException("Unable to calculate a valid QueryRoute");
    }

    private Binding reduceBinding(String type) {
        if (type.isBlank()) {
            return null;
        }
        Binding binding = bindings.get(type);
        if (binding != null) {
            return binding;
        }
        return reduceBinding(type.substring(0, type.lastIndexOf(".")));
    }
}
