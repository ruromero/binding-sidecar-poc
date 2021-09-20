package com.redhat.mercury.binding.model;

public class ObjectRef {

    private String name;

    private String namespace;

    public String getName() {
        return name;
    }

    public ObjectRef setName(String name) {
        this.name = name;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public ObjectRef setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }
}

