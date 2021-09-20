package com.redhat.mercury.binding.model;

public class BindingDefinition {

    public enum BindingType {
        QUERY,
        COMMAND,
        NOTIFICATION,
        SUBSCRIPTION
    }

    private String scopeRef;
    private BindingType type;

    public BindingType getType() {
        return type;
    }

    public BindingDefinition setType(BindingType type) {
        this.type = type;
        return this;
    }

    public String getScopeRef() {
        return scopeRef;
    }

    public BindingDefinition setScopeRef(String scopeRef) {
        this.scopeRef = scopeRef;
        return this;
    }
}
