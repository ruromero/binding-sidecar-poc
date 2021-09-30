package com.redhat.mercury.poc.customercreditrating.services;

import java.util.UUID;

import org.bian.protobuf.InboundBindingService;
import org.bian.protobuf.customercreditrating.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;

import io.cloudevents.v1.proto.CloudEvent;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

@GrpcService
public class CustomerCreditRatingServiceImpl implements InboundBindingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreditRatingServiceImpl.class);
    private static final String CE_RETRIVE_RATING_RESPONSE_TYPE = "org.bian.customercreditrating.retrieve/response";
    private static final Integer FIXED_RATING = 802;

    @Override
    public Uni<CloudEvent> query(CloudEvent request) {
        LOGGER.info("received query request");
        return Uni.createFrom().item(() -> CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setType(CE_RETRIVE_RATING_RESPONSE_TYPE)
                .setSource("http://customer-credit-rating")
                .setProtoData(Any.pack(Rating.newBuilder().setRating(FIXED_RATING).build()))
                .build());
    }

    @Override
    public Uni<CloudEvent> command(CloudEvent request) {
        LOGGER.info("received command request");
        return Uni.createFrom().item(() -> CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setType(CE_RETRIVE_RATING_RESPONSE_TYPE)
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
