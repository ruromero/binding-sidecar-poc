package com.redhat.mercury.binding.services;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.bian.protobuf.customeroffer.BasicReference;
import org.bian.protobuf.customeroffer.CustomerOfferNotification;
import org.bian.protobuf.customeroffer.CustomerReference;
import org.bian.protobuf.customeroffer.FacilityApplicationReference;
import org.junit.jupiter.api.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.redhat.mercury.binding.model.k8s.SubscriptionSpec;
import com.redhat.mercury.binding.test.Profiles.KafkaIntegrationProfile;
import com.redhat.mercury.poc.BianCloudEventConstants;

import io.cloudevents.v1.proto.CloudEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import static com.redhat.mercury.poc.BianCloudEventConstants.CUSTOMER_OFFER_COMPLETED;
import static com.redhat.mercury.poc.BianCloudEventConstants.CUSTOMER_OFFER_INITIATED;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
@TestProfile(KafkaIntegrationProfile.class)
class SubscriptionRoutestTest {

    @Inject
    ConfigurationService configService;

    @Inject
    CamelContext camel;

    @Test
    void testBasicSubscription() throws Exception {

        Collection<SubscriptionSpec> subscriptions = List.of(new SubscriptionSpec()
                .setServiceDomain("customer-offer")
                .setEvents(List.of(CUSTOMER_OFFER_INITIATED, CUSTOMER_OFFER_COMPLETED)));
        configService.updateSubscriptions(subscriptions);

        assertThat(camel.getRoute("subscription.customer-offer"), notNullValue());

        BeanCounter counter = new BeanCounter(5);
        camel.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws InvalidProtocolBufferException {
                from("timer:foo?delay=1")
                        .setBody()
                        .simple(JsonFormat.printer().print(buildNotification()))
                        .to("kafka:CUSTOMER_OFFER?brokers=localhost:9092");

                from("grpc://{{route.grpc.hostservice}}/org.bian.protobuf.InboundBindingService")
                        .log("${body}")
                        .bean(method(counter, "process"));
            }
        });
        counter.count.await(10, TimeUnit.MINUTES);
    }

    private CustomerOfferNotification buildNotification() {
        return CustomerOfferNotification
                .newBuilder()
                .setStatus(CustomerOfferEventDeserializer.OFFER_INITIATED_EVENT_STATUS)
                .setCustomerOfferReference(BasicReference.newBuilder().setId("9").setStatus("pricing_accepted").build())
                .setCustomerReference(CustomerReference.newBuilder().setId("12345").setName("John Doe").build())
                .setFacilityApplicationReference(FacilityApplicationReference.newBuilder().setProductCode("CL").build())
                .setConsumerLoanReference(BasicReference.newBuilder().setId("CLSSR765266").setStatus("INITIALIZED").build())
                .build();
    }

    private static class BeanCounter {

        CountDownLatch count;

        public BeanCounter(int expected) {
            this.count = new CountDownLatch(expected);
        }

        public void process(CloudEvent event) {
            if (BianCloudEventConstants.CUSTOMER_OFFER_INITIATED.equals(event.getType())) {
                count.countDown();
            }
        }
    }

}
