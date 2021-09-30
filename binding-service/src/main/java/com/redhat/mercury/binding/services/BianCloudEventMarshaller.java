package com.redhat.mercury.binding.services;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.bian.protobuf.customercreditrating.Rating;

import com.google.protobuf.InvalidProtocolBufferException;
import com.redhat.mercury.poc.BianCloudEventConstants;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.Builder;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BianCloudEventMarshaller {

    public CloudEvent httpToCloudEvent(Exchange exchange) {
        Builder builder = CloudEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setType(BianCloudEventConstants.CUSTOMER_OFFER_INITIATE);
        String origin = exchange.getProperty("CamelHttpOrigin", null, String.class);
        if (origin != null) {
            builder.setSource(origin);
        }
        return builder.build();
    }

    //TODO: Return object depending on the response cloudeventtype or empty if null
    public String toHttp(CloudEvent cloudEvent) {
        switch (cloudEvent.getType()) {
            default:
                try {
                    return cloudEvent.getProtoData().unpack(Rating.class).toString();
                } catch (InvalidProtocolBufferException e) {
                    return null;
                }
        }
    }
}
