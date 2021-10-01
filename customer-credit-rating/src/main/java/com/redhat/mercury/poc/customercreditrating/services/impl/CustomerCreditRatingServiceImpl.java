package com.redhat.mercury.poc.customercreditrating.services.impl;

import javax.enterprise.context.ApplicationScoped;

import org.bian.protobuf.customercreditrating.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.mercury.poc.customercreditrating.services.CustomerCreditRatingService;

@ApplicationScoped
public class CustomerCreditRatingServiceImpl implements CustomerCreditRatingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreditRatingServiceImpl.class);
    private static final Integer FIXED_RATING = 802;

    @Override
    public Rating retrieveCustomerCreditRating(String sdReferenceId, String crReferenceId) {
        return Rating.newBuilder()
                .setRating(FIXED_RATING)
                .build();
    }
}
