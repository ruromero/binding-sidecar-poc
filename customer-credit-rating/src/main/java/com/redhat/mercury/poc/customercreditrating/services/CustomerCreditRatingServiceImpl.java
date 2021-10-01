package com.redhat.mercury.poc.customercreditrating.services;

import java.util.UUID;

import org.bian.protobuf.InboundBindingService;
import org.bian.protobuf.customercreditrating.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.redhat.mercury.poc.constants.BianCloudEvent;
import com.redhat.mercury.poc.constants.CustomerCreditRating;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

@GrpcService
public class CustomerCreditRatingServiceImpl implements InboundBindingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreditRatingServiceImpl.class);
    private static final Integer FIXED_RATING = 802;

    @Override
    public Uni<CloudEvent> query(CloudEvent request) {
        LOGGER.info("received query request");
        return Uni.createFrom().item(() -> CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setType(CustomerCreditRating.STATE_RETRIEVE)
                .setSource("http://customer-credit-rating")
                .putAttributes(BianCloudEvent.CE_ACTION, CloudEventAttributeValue.newBuilder().setCeString(BianCloudEvent.CE_ACTION_RESPONSE).build())
                .setProtoData(Any.pack(Rating.newBuilder().setRating(FIXED_RATING).build()))
                .build());
    }

    @Override
    public Uni<CloudEvent> command(CloudEvent request) {
        LOGGER.info("received command request");
        return Uni.createFrom().item(() -> CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setType(CustomerCreditRating.STATE_RETRIEVE)
                .setSource("http://customer-credit-rating")
                .setProtoData(Any.pack(Rating.newBuilder().setRating(FIXED_RATING).build()))
                .build());
    }

    @Override
    public Uni<com.google.protobuf.Empty> receive(CloudEvent request) {
        LOGGER.info("received receive request");
        return Uni.createFrom().item(() -> Empty.getDefaultInstance());
    }
}
