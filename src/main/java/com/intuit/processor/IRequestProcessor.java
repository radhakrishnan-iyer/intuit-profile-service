package com.intuit.processor;

import com.intuit.common.model.Request;
import com.intuit.common.model.Response;

public interface IRequestProcessor {
    boolean process(Request request, String correlationId, String customerId);
    Response getTransactionDetails(String correlationId);
    Response getProfileDetails(String profileId);
}
