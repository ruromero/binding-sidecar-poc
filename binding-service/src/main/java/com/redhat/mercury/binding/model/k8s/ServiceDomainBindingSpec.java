package com.redhat.mercury.binding.model.k8s;

import java.io.Serializable;
import java.util.Collection;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ServiceDomainBindingSpec implements Serializable {

    private String serviceDomain;
    private Collection<BindingSpec> bindings;
    private Collection<ExposedScopeSpec> exposedScopes;
    private Collection<SubscriptionSpec> subscriptions;

    public String getServiceDomain() {
        return serviceDomain;
    }

    public ServiceDomainBindingSpec setServiceDomain(String serviceDomain) {
        this.serviceDomain = serviceDomain;
        return this;
    }

    public Collection<BindingSpec> getBindings() {
        return bindings;
    }

    public ServiceDomainBindingSpec setBindings(Collection<BindingSpec> bindings) {
        this.bindings = bindings;
        return this;
    }

    public Collection<ExposedScopeSpec> getExposedScopes() {
        return exposedScopes;
    }

    public ServiceDomainBindingSpec setExposedScopes(Collection<ExposedScopeSpec> exposedScopes) {
        this.exposedScopes = exposedScopes;
        return this;
    }

    public Collection<SubscriptionSpec> getSubscriptions() {
        return subscriptions;
    }

    public ServiceDomainBindingSpec setSubscriptions(Collection<SubscriptionSpec> subscriptions) {
        this.subscriptions = subscriptions;
        return this;
    }

    @Override
    public String toString() {
        return "ServiceDomainBindingSpec{" +
                "serviceDomain='" + serviceDomain + '\'' +
                ", bindings=" + bindings +
                ", exposedScopes=" + exposedScopes +
                ", subscriptions=" + subscriptions +
                '}';
    }
}
