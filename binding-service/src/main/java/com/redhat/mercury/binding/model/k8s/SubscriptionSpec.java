package com.redhat.mercury.binding.model.k8s;

import java.util.Collection;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SubscriptionSpec {

    private String serviceDomain;
    private Collection<String> events;

    public String getServiceDomain() {
        return serviceDomain;
    }

    public SubscriptionSpec setServiceDomain(String serviceDomain) {
        this.serviceDomain = serviceDomain;
        return this;
    }

    public Collection<String> getEvents() {
        return events;
    }

    public SubscriptionSpec setEvents(Collection<String> events) {
        this.events = events;
        return this;
    }

    @Override
    public String toString() {
        return "SubscriptionSpec{" +
                "serviceDomain='" + serviceDomain + '\'' +
                ", events=" + events +
                '}';
    }
}
