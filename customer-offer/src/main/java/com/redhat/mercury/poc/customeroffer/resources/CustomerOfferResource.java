package com.redhat.mercury.poc.customeroffer.resources;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.bian.protobuf.OutboundBindingService;
import org.bian.protobuf.customercreditrating.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.redhat.mercury.poc.BianCloudEventConstants;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;

@Path("/customer-offer")
public class CustomerOfferResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOfferResource.class);

    @GrpcClient
    OutboundBindingService bindingService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Rating> doSomething(@PathParam("id") String id) {
        return bindingService.query(buildRetrieveCloudEvent(id)).onItem().transform(reply -> {
            try {
                if (reply == null || reply.getProtoData() == null) {
                    throw new NotFoundException();
                }
                return reply.getProtoData().unpack(Rating.class);
            } catch (InvalidProtocolBufferException e) {
                LOGGER.error("Exception trying to unpack the rating", e);
                throw new ServerErrorException("Unable to unpack the requested rating", Status.INTERNAL_SERVER_ERROR, e);
            }
        });
    }

    private CloudEvent buildRetrieveCloudEvent(String id) {
        return CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSource("http://customer-offer")
                .setType(BianCloudEventConstants.CUSTOMER_CREDIT_RATING_STATE_RETRIEVE)
                .putAttributes(BianCloudEventConstants.CE_CR_REF, CloudEventAttributeValue.newBuilder().setCeString(id).build())
                .build();
    }
}
