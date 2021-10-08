package com.redhat.mercury.myprp.services.impl;

import static com.redhat.mercury.myprp.services.impl.PartyRoutingService.COMPLETED_STATUS;
import static com.redhat.mercury.myprp.services.impl.PartyRoutingService.INITIATED_STATUS;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.mercury.customeroffer.events.CustomerOfferNotificationService;

import org.bian.protobuf.customeroffer.CustomerOfferNotification;


@ApplicationScoped
public class CustomerOfferEventHandler implements CustomerOfferNotificationService {

    @Inject
    PartyRoutingService svc;

    @Override
    public void onCustomerOfferInitiated(CustomerOfferNotification notification) {
        svc.updatePartyRoutingState(INITIATED_STATUS, notification.getCustomerOfferReference().getId());
    }

    @Override
    public void onCustomerOfferCompleted(CustomerOfferNotification notification) {
        svc.updatePartyRoutingState(COMPLETED_STATUS, notification.getCustomerOfferReference().getId());
    }

}
