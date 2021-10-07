package com.redhat.mercury.customercreditrating.services.impl;

import javax.enterprise.context.ApplicationScoped;

import org.bian.protobuf.customercreditrating.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.mercury.customercreditrating.services.CustomerCreditRatingService;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class CustomerCreditRatingServiceImpl implements CustomerCreditRatingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreditRatingServiceImpl.class);
    private static final Integer FIXED_RATING = 802;

    @Override
    public Uni<Rating> retrieveCustomerCreditRatingState(String sd, String cr) {
        LOGGER.info("retrieveCustomerCreditRatingState received");
        return Uni.createFrom().item(() -> Rating.newBuilder()
                .setRating(FIXED_RATING)
                .build());
    }
}
