package com.redhat.mercury.binding.model;

public class BindingDefinition {

    public enum Action {
        query,
        command,
        notify,
        subscription
    }

    private String domainName;
    private String scopeRef;
    private Action action;

    public String getDomainName() {
        return domainName;
    }

    public BindingDefinition setDomainName(String domainName) {
        this.domainName = domainName;
        return this;
    }

    public Action getAction() {
        return action;
    }

    public BindingDefinition setAction(Action action) {
        this.action = action;
        return this;
    }

    public String getScopeRef() {
        return scopeRef;
    }

    public BindingDefinition setScopeRef(String scopeRef) {
        this.scopeRef = scopeRef;
        return this;
    }

    @Override
    public String toString() {
        return "BindingDefinition{" +
                "domainName='" + domainName + '\'' +
                ", scopeRef='" + scopeRef + '\'' +
                ", action=" + action +
                '}';
    }
}
