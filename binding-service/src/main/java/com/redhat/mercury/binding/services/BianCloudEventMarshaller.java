package com.redhat.mercury.binding.services;

import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.bian.protobuf.ExternalRequest;
import org.bian.protobuf.ExternalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.redhat.mercury.constants.PartyRoutingProfile;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BianCloudEventMarshaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(BianCloudEventMarshaller.class);

    private static final Pattern PARTY_ROUTING_PROFILE_PARTY_STATE_STATUS_RETRIEVE_PATH = Pattern.compile("/" + PartyRoutingProfile.DOMAIN_NAME + "/([a-zA-Z0-9\\-]+)/party-state/([a-zA-Z0-9\\-]+)/status/([a-zA-Z0-9\\-]+)");

    public ExternalRequest httpToExternalRequest(Exchange exchange) {
        ExternalRequest.Builder reqBuilder = ExternalRequest.newBuilder()
                .setPath(exchange.getMessage().getHeader(Exchange.HTTP_URI, String.class))
                .setVerb(exchange.getMessage().getHeader(Exchange.HTTP_METHOD, String.class));
        if (exchange.getMessage().getBody() != null) {
            reqBuilder.setPayload(ByteString.copyFrom(exchange.getMessage(String.class).getBytes()));
        }
        return reqBuilder.build();
    }

    public void toHttp(Exchange exchange, ExternalResponse response) {
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, response.getResponseCode());
        if (response.getPayload() != null && !response.getPayload().isEmpty()) {
            exchange.getMessage().setBody(response.getPayload().toString());
        }
    }


}
