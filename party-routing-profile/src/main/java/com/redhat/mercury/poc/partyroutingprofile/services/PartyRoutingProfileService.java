package com.redhat.mercury.poc.partyroutingprofile.services;

import org.bian.protobuf.customeroffer.CustomerOfferNotification;
import org.bian.protobuf.partyroutingprofile.PartyRoutingStateList;

public interface PartyRoutingProfileService {

    PartyRoutingStateList retrievePartyStateStatus(String sdRef, String crRef, String bqRef);

    void onCustomerOfferInitiated(CustomerOfferNotification notification);

    void onCustomerOfferCompleted(CustomerOfferNotification notification);

}
