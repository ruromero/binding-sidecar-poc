package com.redhat.mercury.binding.services;

import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.bian.protobuf.customeroffer.CustomerOfferNotification;
import org.bian.protobuf.customeroffer.CustomerOfferNotification.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.redhat.mercury.poc.constants.BianCloudEvent;
import com.redhat.mercury.poc.constants.CustomerOffer;

import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;

public class CustomerOfferEventDeserializer implements Deserializer<CloudEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOfferEventDeserializer.class);
    public static final String OFFER_INITIATED_EVENT_STATUS = "OFFER_INITIATED";
    public static final String OFFER_COMPLETED_EVENT_STATUS = "OFFER_COMPLETED";
    public static final String CUSTOMER_OFFER_SOURCE = "CustomerOffer";

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public CloudEvent deserialize(String s, byte[] bytes) {
        Builder notificationBuilder = CustomerOfferNotification.newBuilder();
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(new String(bytes), notificationBuilder);
            CustomerOfferNotification notification = notificationBuilder.build();
            CloudEvent.Builder ceBuilder = CloudEvent.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setProtoData(Any.pack(notification));
            switch (notification.getStatus()) {
                case OFFER_COMPLETED_EVENT_STATUS:
                    ceBuilder.setType(CustomerOffer.CUSTOMER_OFFER_COMPLETED);
                    break;
                case OFFER_INITIATED_EVENT_STATUS:
                    ceBuilder.setType(CustomerOffer.CUSTOMER_OFFER_INITIATED);
                    break;
                default:
                    LOGGER.warn("Unknown notification status {}", notification.getStatus());
            }
            return ceBuilder.setSource(CUSTOMER_OFFER_SOURCE)
                    .putAttributes(BianCloudEvent.CE_CR_REF, CloudEventAttributeValue.newBuilder()
                            .setCeString(notification.getCustomerReference().getId())
                            .build())
                    .build();

        } catch (InvalidProtocolBufferException e) {
            LOGGER.error("Unable to deserialize CustomerOfferEvent", e);
        }
        return null;
    }

    @Override
    public CloudEvent deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
