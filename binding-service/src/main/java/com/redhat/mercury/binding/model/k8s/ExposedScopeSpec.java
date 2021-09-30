package com.redhat.mercury.binding.model.k8s;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ExposedScopeSpec {

    private String scopeRef;
    private String action;

    public String getScopeRef() {
        return scopeRef;
    }

    public ExposedScopeSpec setScopeRef(String scopeRef) {
        this.scopeRef = scopeRef;
        return this;
    }

    public String getAction() {
        return action;
    }

    public ExposedScopeSpec setAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public String toString() {
        return "ExposedScopeSpec{" +
                "scopeRef='" + scopeRef + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
