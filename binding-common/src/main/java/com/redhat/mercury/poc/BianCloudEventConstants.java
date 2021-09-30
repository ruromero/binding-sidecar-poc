package com.redhat.mercury.poc;

public final class BianCloudEventConstants {

    public static final String CE_TYPE_PREFIX = "org.bian.";
    public static final String PARTY_ROUTING_PROFILE_RETRIEVE_RESPONSE = "org.bian.partyroutingprofile.retrieve.response";
    public static final String CUSTOMER_CREDIT_RATING_STATE_RETRIEVE = "org.bian.customercreditrating.state.query";
    public static final String CUSTOMER_OFFER_INITIATE = "org.bian.customeroffer.initiate";
    public static final String CUSTOMER_OFFER_INITIATED = "org.bian.customeroffer.initiated";
    public static final String CUSTOMER_OFFER_COMPLETED = "org.bian.customeroffer.completed";

    public static final String CE_CR_REF = "ce_biancrref";
    public static final String CE_SD_REF = "ce_biansdref";

    private BianCloudEventConstants() {
    }
}
