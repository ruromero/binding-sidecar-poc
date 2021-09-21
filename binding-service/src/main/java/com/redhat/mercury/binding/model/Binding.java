package com.redhat.mercury.binding.model;

public class Binding {

    private BindingDefinition definition;

    private String endpoint;

    public BindingDefinition getDefinition() {
        return definition;
    }

    public Binding setDefinition(BindingDefinition definition) {
        this.definition = definition;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Binding setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    @Override
    public String toString() {
        return "Binding{" +
                "definition=" + definition +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
