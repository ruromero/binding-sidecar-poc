package com.redhat.mercury.binding.services;

import org.apache.camel.Exchange;

import io.cloudevents.v1.proto.CloudEvent;

public class BianCloudEventMarshaller {

    public CloudEvent httpToCloudEvent(Exchange exchange) {
        return CloudEvent.newBuilder().build();
    }
}
