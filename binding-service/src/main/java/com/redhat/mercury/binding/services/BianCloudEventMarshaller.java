package com.redhat.mercury.binding.services;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.bian.protobuf.customercreditrating.Rating;
import org.bian.protobuf.partyroutingprofile.PartyRoutingState;
import org.bian.protobuf.partyroutingprofile.PartyRoutingStateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpMethods;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.redhat.mercury.poc.constants.CustomerCreditRating;
import com.redhat.mercury.poc.constants.PartyRoutingProfile;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.Builder;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import io.quarkus.runtime.annotations.RegisterForReflection;

import static com.redhat.mercury.poc.constants.BianCloudEvent.CE_ACTION;
import static com.redhat.mercury.poc.constants.BianCloudEvent.CE_ACTION_COMMAND;
import static com.redhat.mercury.poc.constants.BianCloudEvent.CE_ACTION_QUERY;
import static com.redhat.mercury.poc.constants.BianCloudEvent.CE_BQ_REF;
import static com.redhat.mercury.poc.constants.BianCloudEvent.CE_CR_REF;
import static com.redhat.mercury.poc.constants.BianCloudEvent.CE_SD_REF;

@RegisterForReflection
public class BianCloudEventMarshaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(BianCloudEventMarshaller.class);

    private static final Pattern PARTY_ROUTING_PROFILE_PARTY_STATE_STATUS_RETRIEVE_PATH = Pattern.compile("/" + PartyRoutingProfile.DOMAIN_NAME + "/([a-zA-Z0-9\\-]+)/party-state/([a-zA-Z0-9\\-]+)/status/([a-zA-Z0-9\\-]+)");
    private static final Pattern CUSTOMER_CREDIT_RATING_STATE_RETRIEVE = Pattern.compile("/" + CustomerCreditRating.DOMAIN_NAME + "/([a-zA-Z0-9\\-]+)/customer-credit-rating-state/([a-zA-Z0-9\\-]+)");

    private static final Map<Pattern, String> PATH_MAPPINGS = Map.of(
            PARTY_ROUTING_PROFILE_PARTY_STATE_STATUS_RETRIEVE_PATH, PartyRoutingProfile.PARTY_STATE_STATUS_RETRIEVE,
            CUSTOMER_CREDIT_RATING_STATE_RETRIEVE, CustomerCreditRating.STATE_RETRIEVE
    );

    private static final Map<String, Supplier<Message.Builder>> IN_TYPE_MAPPINGS = Map.of(
            //TODO: Add mappings
    );

    private static final Map<String, Class> OUT_TYPE_MAPPINGS = Map.of(
            PartyRoutingProfile.PARTY_STATE_STATUS_RETRIEVE, PartyRoutingStateList.class,
            CustomerCreditRating.STATE_RETRIEVE, Rating.class
    );

    public CloudEvent httpToCloudEvent(Exchange exchange) throws InvalidProtocolBufferException {
        Builder builder = CloudEvent.newBuilder().setId(UUID.randomUUID().toString());
        String uri = exchange.getMessage().getHeader(Exchange.HTTP_URI, String.class);
        Optional<Matcher> path = getMatchingPath(uri);
        if (!path.isEmpty()) {
            String type = PATH_MAPPINGS.get(path.get().pattern());
            addRefToCE(builder, path.get(), 1, CE_SD_REF);
            addRefToCE(builder, path.get(), 2, CE_CR_REF);
            addRefToCE(builder, path.get(), 3, CE_BQ_REF);
            String action = getCeAction(exchange.getMessage().getHeader(Exchange.HTTP_METHOD, String.class));
            builder.setType(type).putAttributes(CE_ACTION, CloudEventAttributeValue.newBuilder().setCeString(action).build());
            LOGGER.debug("Set ACTION {}", action);
            if (CE_ACTION_COMMAND.equals(action)) {
                if (IN_TYPE_MAPPINGS.containsKey(type)) {
                    Message.Builder messageBuilder = IN_TYPE_MAPPINGS.get(type).get();
                    JsonFormat.parser().ignoringUnknownFields().merge(exchange.getMessage().getBody(String.class), builder);
                    builder.setProtoData(Any.pack(messageBuilder.build()));
                }
            }
        }
        CloudEvent ce = builder.build();
        LOGGER.debug("Converted HTTP request to {} into CloudEvent of type {}", uri, ce.getType());
        return ce;
    }

    public String toHttp(CloudEvent cloudEvent) {
        if (OUT_TYPE_MAPPINGS.containsKey(cloudEvent.getType())) {
            try {
                return cloudEvent.getProtoData().unpack(OUT_TYPE_MAPPINGS.get(cloudEvent.getType())).toString();
            } catch (InvalidProtocolBufferException e) {
                LOGGER.error("Unable to convert to Json response", e);
                return null;
            }
        }
        LOGGER.warn("No response mapping has been defined for CloudEvent {}", cloudEvent.getType());
        return null;
    }

    private void addRefToCE(Builder builder, Matcher matcher, int group, String ref) {
        if(matcher.groupCount() >= group) {
            String value = matcher.group(group);
            LOGGER.debug("Set {} to {}", ref, value);
            builder.putAttributes(ref, CloudEventAttributeValue.newBuilder().setCeString(value).build());
        }
    }

    private Optional<Matcher> getMatchingPath(String uri) {
        return PATH_MAPPINGS
                .keySet()
                .stream()
                .map(p -> p.matcher(uri))
                .filter(m -> m.matches())
                .findFirst();
    }

    private String getCeAction(String method) {
        switch (method) {
            case HttpMethods.GET:
                return CE_ACTION_QUERY;
            default:
                return CE_ACTION_COMMAND;
        }
    }

}
