package com.redhat.mercury.poc.partyroutingprofile.services.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.bian.protobuf.customeroffer.CustomerOfferNotification;
import org.bian.protobuf.partyroutingprofile.PartyRoutingState;
import org.bian.protobuf.partyroutingprofile.PartyRoutingStateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.mercury.poc.partyroutingprofile.services.PartyRoutingProfileService;

@ApplicationScoped
public class PartyRoutingProfileServiceImpl implements PartyRoutingProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartyRoutingProfileServiceImpl.class);
    private static final Map<String, Set<PartyRoutingState>> partyRoutings = new ConcurrentHashMap<>();

    @Override
    public PartyRoutingStateList retrievePartyStateStatus(String sdRef, String crRef, String bqRef) {
        PartyRoutingStateList.Builder resultBuilder = PartyRoutingStateList.newBuilder();
        if (crRef != null) {
            resultBuilder.addAllPartyRoutingStates(partyRoutings.getOrDefault(crRef, Set.of()));
        }
        return resultBuilder.build();
    }

    @Override
    public void onCustomerOfferInitiated(CustomerOfferNotification notification) {
        updatePartyRoutingState(notification, "0", notification.getCustomerOfferReference().getId());
    }

    @Override
    public void onCustomerOfferCompleted(CustomerOfferNotification notification) {
        updatePartyRoutingState(notification, "1", null);
    }

    private void updatePartyRoutingState(CustomerOfferNotification notification, String status, String processId) {
        Set<PartyRoutingState> states = partyRoutings.computeIfAbsent(notification.getCustomerReference().getId(), k -> new HashSet<>());
        states.removeIf(e -> notification.getCustomerOfferReference().getId().equals(e.getProcessId()));
        states.add(PartyRoutingState.newBuilder().setCustomerOfferStatus(status).setProcessId(processId).build());
    }
}
