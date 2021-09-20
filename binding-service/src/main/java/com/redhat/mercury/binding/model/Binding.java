package com.redhat.mercury.binding.model;

public class Binding {

    private BindingDefinition definition;

    private ObjectRef objectRef;

    public BindingDefinition getDefinition() {
        return definition;
    }

    public Binding setDefinition(BindingDefinition definition) {
        this.definition = definition;
        return this;
    }

    public ObjectRef getObjectRef() {
        return objectRef;
    }

    public Binding setObjectRef(ObjectRef objectRef) {
        this.objectRef = objectRef;
        return this;
    }
}
