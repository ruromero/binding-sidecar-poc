package com.redhat.mercury.customeroffer.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bian.protobuf.customercreditrating.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.mercury.customercreditrating.services.CustomerCreditRatingService;

import io.smallrye.mutiny.Uni;

@Path("/customer-offer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerOfferExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOfferExample.class);

    @Inject
    CustomerCreditRatingService ccrClient;

    @GET()
    @Path("/{id}")
    public Uni<Rating> get(@PathParam("id") String id) {
        LOGGER.info("Query operation");
        //TODO: Add some more logic
        return ccrClient.retrieveCustomerCreditRatingState("not used", id);
    }

}
