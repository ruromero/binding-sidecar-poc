package com.redhat.mercury.poc.customeroffer.resources;

import java.util.UUID;

import javax.ws.rs.NotFoundException;

import org.bian.protobuf.InboundBindingService;
import org.bian.protobuf.OutboundBindingService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Empty;
import com.redhat.mercury.poc.BianCloudEventConstants;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

@GrpcService
public class CustomerOfferInboundService implements InboundBindingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOfferInboundService.class);
    private static final String CUSTOMER_OFFER_SOURCE = "org.bian.customeroffer";

    @GrpcClient
    OutboundBindingService outbound;

    @Override
    public Uni<CloudEvent> query(CloudEvent request) {
        LOGGER.info("Query operation");
        //TODO: Add some more logic
        return Uni.createFrom()
                .item(request)
                .call(() -> outbound.query(buildRetrieveCloudEvent(request)));
    }

    @Override
    public Uni<CloudEvent> command(CloudEvent request) {
        LOGGER.info("Command operation");
        return null;
    }

    @Override
    public Uni<Empty> receive(CloudEvent request) {
        LOGGER.info("Receive operation");
        return null;
    }

    private CloudEvent buildRetrieveCloudEvent(CloudEvent source) {
        return CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSource(CUSTOMER_OFFER_SOURCE)
                .setType(BianCloudEventConstants.CUSTOMER_CREDIT_RATING_STATE_RETRIEVE)
                .putAttributes(BianCloudEventConstants.CE_CR_REF, CloudEventAttributeValue
                        .newBuilder()
                        .setCeString(source.getAttributesOrThrow(BianCloudEventConstants.CE_CR_REF)
                                .getCeString())
                        .build())
                .build();
    }
}
