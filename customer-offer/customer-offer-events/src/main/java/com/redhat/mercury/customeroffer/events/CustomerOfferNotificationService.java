package com.redhat.mercury.customeroffer.events;

import org.bian.protobuf.customeroffer.CustomerOfferNotification;

public interface CustomerOfferNotificationService {

    void onCustomerOfferInitiated(CustomerOfferNotification notification);

    void onCustomerOfferCompleted(CustomerOfferNotification notification);

    //TODO: Implement other events
}
