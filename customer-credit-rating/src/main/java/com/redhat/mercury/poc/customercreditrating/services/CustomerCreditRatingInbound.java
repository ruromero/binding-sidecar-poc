package com.redhat.mercury.poc.customercreditrating.services;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;
import com.redhat.mercury.poc.business.service.BaseInboundBindingService;
import com.redhat.mercury.poc.constants.CustomerCreditRating;

import io.cloudevents.v1.proto.CloudEvent;
import io.quarkus.grpc.GrpcService;

import static com.redhat.mercury.poc.constants.BianCloudEvent.CE_CR_REF;
import static com.redhat.mercury.poc.constants.BianCloudEvent.CE_SD_REF;

@GrpcService
public class CustomerCreditRatingInbound extends BaseInboundBindingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreditRatingInbound.class);

    @Inject
    CustomerCreditRatingService service;

    protected final Message mapQueryMethod(CloudEvent cloudEvent) {
        switch (cloudEvent.getType()) {
            case CustomerCreditRating.STATE_RETRIEVE:
                return service.retrieveCustomerCreditRating(getRef(cloudEvent, CE_SD_REF), getRef(cloudEvent, CE_CR_REF));
            //TODO: Implement
        }
        return null;
    }

    protected final Message mapCommandMethod(CloudEvent cloudEvent) {
        //TODO: Implement
        return null;
    }

    protected final void mapReceiveMethod(CloudEvent cloudEvent) {
        //TODO: Implement
    }
}
