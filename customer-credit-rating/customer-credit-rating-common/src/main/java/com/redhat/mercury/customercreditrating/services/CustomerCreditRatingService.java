package com.redhat.mercury.customercreditrating.services;

import org.bian.protobuf.customercreditrating.Rating;

import io.smallrye.mutiny.Uni;

public interface CustomerCreditRatingService {

    Uni<Rating> retrieveCustomerCreditRatingState(String sd, String cr);

    //TODO: Implement other queries and commands
}
