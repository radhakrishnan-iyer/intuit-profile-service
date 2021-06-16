package com.intuit.controller;

import com.intuit.common.constant.Constants;
import com.intuit.common.model.Request;
import com.intuit.common.model.Response;
import com.intuit.processor.IRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping(value = "/profile", produces = "application/json", consumes = "application/json")
public class ProfileManagementController {

    private static Logger logger = LoggerFactory.getLogger(ProfileManagementController.class);

    @Autowired
    private IRequestProcessor requestProcessor;

    @Autowired
    private ExecutorService requestExecutorService;

    @RequestMapping(value = "/create" , method = RequestMethod.POST)
    public ResponseEntity<Response> createProfile(@RequestBody Request request) {
        String correlationId = MDC.get(Constants.request_id);
        String customerId = MDC.get(Constants.customer_id);
        logger.info("Request Id : {}" , correlationId );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(Constants.request_id , correlationId);
        httpHeaders.set(Constants.customer_id, customerId);

        requestExecutorService.execute(() -> requestProcessor.process(request , correlationId, customerId));

        Response response = new Response();
        response.setCorrelationId(request.getCorrelationId());
        response.setMessage(Constants.REQUEST_ACCEPTED);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .headers(httpHeaders)
                .body(response);
    }

    @RequestMapping(value = "/update" , method = RequestMethod.POST)
    public ResponseEntity<Response> updateProfile(@RequestBody Request request) {
        String correlationId = MDC.get(Constants.request_id);
        String customerId = MDC.get(Constants.customer_id);
        logger.info("Request Id : {}" , correlationId );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(Constants.request_id , correlationId);
        httpHeaders.set(Constants.customer_id, customerId);

        requestExecutorService.execute(() -> requestProcessor.process(request , correlationId, customerId));

        Response response = new Response();
        response.setCorrelationId(request.getCorrelationId());
        response.setMessage(Constants.REQUEST_ACCEPTED);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .headers(httpHeaders)
                .body(response);
    }

    @RequestMapping(value = "/getTransactionStatus" , method = RequestMethod.POST)
    public ResponseEntity<Response> getTransactionStatus(@RequestBody Request request) {
        Response response = requestProcessor.getTransactionDetails(request.getCorrelationId());
        response.setCorrelationId(request.getCorrelationId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @RequestMapping(value = "/getProfile" , method = RequestMethod.POST)
    public ResponseEntity<Response> getProfileById(@RequestBody Request request) {
        String profileId = request.getProfile().getProfileId();
        logger.info("Getting profile by id {}" , profileId);
        Response response = requestProcessor.getProfileDetails(profileId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
