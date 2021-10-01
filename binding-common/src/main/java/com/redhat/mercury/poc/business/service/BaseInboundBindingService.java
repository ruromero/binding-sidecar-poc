package com.redhat.mercury.poc.business.service;

import java.util.UUID;

import org.bian.protobuf.InboundBindingService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import com.redhat.mercury.poc.constants.BianCloudEvent;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import io.smallrye.mutiny.Uni;

public abstract class BaseInboundBindingService implements InboundBindingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseInboundBindingService.class);

    @ConfigProperty(name = "mercury.servicedomain")
    String serviceDomainName;

    @Override
    public Uni<CloudEvent> query(CloudEvent request) {
        LOGGER.info("received query request");
        return Uni.createFrom().item(() -> CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setType(request.getType())
                .setSource("http://" + serviceDomainName)
                .putAttributes(BianCloudEvent.CE_ACTION, CloudEventAttributeValue.newBuilder().setCeString(BianCloudEvent.CE_ACTION_RESPONSE).build())
                .setProtoData(Any.pack(mapQueryMethod(request)))
                .build());
    }

    @Override
    public Uni<CloudEvent> command(CloudEvent request) {
        LOGGER.info("received command request");
        return Uni.createFrom().item(() -> CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setType(request.getType())
                .setSource("http://" + serviceDomainName)
                .putAttributes(BianCloudEvent.CE_ACTION, CloudEventAttributeValue.newBuilder().setCeString(BianCloudEvent.CE_ACTION_RESPONSE).build())
                .setProtoData(Any.pack(mapCommandMethod(request)))
                .build());
    }

    @Override
    public Uni<Empty> receive(CloudEvent request) {
        LOGGER.info("received receive request");
        return Uni.createFrom().item(() -> {
            mapReceiveMethod(request);
            return Empty.getDefaultInstance();
        });
    }

    protected abstract Message mapQueryMethod(CloudEvent cloudEvent);

    protected abstract Message mapCommandMethod(CloudEvent cloudEvent);

    protected abstract void mapReceiveMethod(CloudEvent cloudEvent);

    protected String getRef(CloudEvent cloudEvent, String ref) {
        CloudEventAttributeValue value = cloudEvent.getAttributesMap().get(ref);
        if (value == null) {
            return null;
        }
        return value.getCeString();
    }

}
