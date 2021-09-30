package com.redhat.mercury.poc.partyroutingprofile.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bian.protobuf.InboundBindingService;
import org.bian.protobuf.customeroffer.CustomerOfferNotification;
import org.bian.protobuf.partyroutingprofile.PartyRoutingState;
import org.bian.protobuf.partyroutingprofile.PartyRoutingStateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.Builder;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

import static com.redhat.mercury.poc.BianCloudEventConstants.CE_CR_REF;
import static com.redhat.mercury.poc.BianCloudEventConstants.CUSTOMER_OFFER_COMPLETED;
import static com.redhat.mercury.poc.BianCloudEventConstants.CUSTOMER_OFFER_INITIATED;
import static com.redhat.mercury.poc.BianCloudEventConstants.PARTY_ROUTING_PROFILE_RETRIEVE_RESPONSE;

@GrpcService
public class PartyRoutingProfileServiceImpl implements InboundBindingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartyRoutingProfileServiceImpl.class);
    private static final Map<String, Set<PartyRoutingState>> partyRoutings = new ConcurrentHashMap<>();

    @Override
    public Uni<CloudEvent> query(CloudEvent request) {
        LOGGER.info("received query request");
        return Uni.createFrom().item(() -> {
            Builder eventBuilder = CloudEvent.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setType(PARTY_ROUTING_PROFILE_RETRIEVE_RESPONSE)
                    .setSource("http://party-routing-profile");
            CloudEventAttributeValue cr = request.getAttributesMap().get(CE_CR_REF);
            PartyRoutingStateList.Builder resultBuilder = PartyRoutingStateList.newBuilder();
            if (cr != null) {
                resultBuilder.addAllPartyRoutingStates(partyRoutings.getOrDefault(cr.getCeString(), Set.of()));
            }
            return eventBuilder.setProtoData(Any.pack(resultBuilder.build())).build();
        });
    }

    @Override
    public Uni<CloudEvent> command(CloudEvent request) {
        LOGGER.info("received command request");
        return Uni.createFrom().item(() -> CloudEvent.newBuilder().build());
    }

    @Override
    public Uni<com.google.protobuf.Empty> receive(CloudEvent request) {
        LOGGER.info("received receive request");
        return Uni.createFrom().item(() -> {
            try {
                CustomerOfferNotification notification = request.getProtoData().unpack(CustomerOfferNotification.class);
                switch (request.getType()) {
                    case CUSTOMER_OFFER_INITIATED:
                        updatePartyRoutingState(notification, "0", notification.getCustomerOfferReference().getId());
                        break;
                    case CUSTOMER_OFFER_COMPLETED:
                        updatePartyRoutingState(notification, "1", null);
                        break;
                    default:
                        LOGGER.info("Ignoring unexpected notification type: {}", request.getType());
                }
            } catch (InvalidProtocolBufferException e) {
                LOGGER.error("Unable to unpack CustomerOfferNotification", e);
            }
            return Empty.getDefaultInstance();
        });
    }

    private void updatePartyRoutingState(CustomerOfferNotification notification, String status, String processId) {
        Set<PartyRoutingState> states = partyRoutings.computeIfAbsent(notification.getCustomerReference().getId(), k -> new HashSet<>());
        states.removeIf(e -> notification.getCustomerOfferReference().getId().equals(e.getProcessId()));
        states.add(PartyRoutingState.newBuilder().setCustomerOfferStatus(status).setProcessId(processId).build());
    }

}
