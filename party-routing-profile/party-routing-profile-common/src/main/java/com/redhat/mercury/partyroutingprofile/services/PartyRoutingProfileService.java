package com.redhat.mercury.partyroutingprofile.services;

import org.bian.protobuf.partyroutingprofile.PartyRoutingStateList;

import io.smallrye.mutiny.Uni;

public interface PartyRoutingProfileService {

    Uni<PartyRoutingStateList> retrievePartyStateStatus(String sdRef, String crRef, String bqRef);

}
