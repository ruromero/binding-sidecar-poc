package com.redhat.mercury.binding.model.k8s;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("mercury.redhat.io")
@Version("v1alpha1")
public class ServiceDomainBinding extends CustomResource<ServiceDomainBindingSpec, Void> implements Namespaced {

    public ServiceDomainBinding() {
        super();
    }

    public ServiceDomainBinding(String metaName, ServiceDomainBindingSpec spec) {
        setMetadata(new ObjectMetaBuilder().withName(metaName).build());
        setSpec(spec);
    }
}
