package com.redhat.mercury.poc.customercreditrating.services;

import org.bian.protobuf.customercreditrating.Rating;

public interface CustomerCreditRatingService {

    Rating retrieveCustomerCreditRating(String sdReferenceId, String crReferenceId);

    //TODO: Add other operations here
}
