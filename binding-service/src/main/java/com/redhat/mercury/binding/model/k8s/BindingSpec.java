package com.redhat.mercury.binding.model.k8s;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BindingSpec implements Serializable {

    private String serviceDomain;
    private String scopeRef;
    private String action;

    public String getServiceDomain() {
        return serviceDomain;
    }

    public BindingSpec setServiceDomain(String serviceDomain) {
        this.serviceDomain = serviceDomain;
        return this;
    }

    public String getScopeRef() {
        return scopeRef;
    }

    public BindingSpec setScopeRef(String scopeRef) {
        this.scopeRef = scopeRef;
        return this;
    }

    public String getAction() {
        return action;
    }

    public BindingSpec setAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public String toString() {
        return "BindingSpec{" +
                "serviceDomain='" + serviceDomain + '\'' +
                ", scopeRef='" + scopeRef + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
