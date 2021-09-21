package com.redhat.mercury.binding.model.k8s;

import java.io.Serializable;
import java.util.Collection;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ServiceDomainBindingSpec implements Serializable {

    private String serviceDomain;
    private Collection<BindingSpec> bindings;

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
}
