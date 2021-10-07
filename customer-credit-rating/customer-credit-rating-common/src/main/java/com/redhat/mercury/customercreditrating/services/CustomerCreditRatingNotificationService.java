package com.redhat.mercury.customercreditrating.services;

import org.bian.protobuf.customercreditrating.RatingEvent;

public interface CustomerCreditRatingNotificationService {

    void onCreditRatingStateEvent(RatingEvent event);

    //TODO: Implement other events
}
