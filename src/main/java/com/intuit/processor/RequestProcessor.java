package com.intuit.processor;

import com.intuit.cache.CacheManager;
import com.intuit.common.constant.Constants;
import com.intuit.common.model.Request;
import com.intuit.common.model.Response;
import com.intuit.common.model.profile.Profile;
import com.intuit.dao.DBUtil;
import com.intuit.dao.IProfileDao;
import com.intuit.dao.ITransactionDao;
import com.intuit.dao.model.Transaction;
import com.intuit.dao.model.TransactionStatus;
import com.intuit.service.QueuePublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;

import static com.intuit.common.constant.Constants.*;

public class RequestProcessor implements IRequestProcessor {
    private static Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    private final QueuePublishService queuePublishService;
    private final ITransactionDao transactionDao;
    private final IProfileDao profileDao;
    private final CacheManager cacheManager;
    private final String insertTransactionQuery;
    private final String getTransactionDetailsQuery;

    public RequestProcessor(QueuePublishService queuePublishService, ITransactionDao transactionDao , IProfileDao profileDao, CacheManager cacheManager , String insertTransactionQuery, String getTransactionDetailsQuery) {
        this.queuePublishService = queuePublishService;
        this.transactionDao = transactionDao;
        this.profileDao = profileDao;
        this.cacheManager = cacheManager;
        this.insertTransactionQuery = insertTransactionQuery;
        this.getTransactionDetailsQuery = getTransactionDetailsQuery;
    }

    @Override
    public boolean process(Request request, String correlationId, String customerId) {
        try {
            MDC.clear();
            MDC.put(Constants.request_id, correlationId);
            MDC.put(Constants.customer_id, customerId);

            logger.info("Processing the request : {}", request);
            Transaction transaction = DBUtil.createNewTransaction(request, correlationId, customerId);

            // Insert into DB
            transactionDao.insert(transaction, insertTransactionQuery);
            // Insert transaction into DB
            cacheManager.addTransaction(transaction);

            //Publish the request asynchronously on queue so that subscription service can process it
            queuePublishService.publish(request);

            return true;
        }
        catch (Exception ex) {
            logger.error("Exception while processing the request {}" , request , ex);
            throw ex;
        }
    }

    @Override
    public Response getTransactionDetails(String correlationId) {
        Optional<Transaction> transactionOptional = cacheManager.getTransaction(correlationId);
        if(!transactionOptional.isPresent()) {
            logger.info("Transaction not found in cache hence trying to fetch from DB");
            transactionOptional = transactionDao.getLatestTransaction(correlationId , getTransactionDetailsQuery);
            if(transactionOptional.isPresent()) {
                logger.info("Transaction found in database");
                Transaction transaction = transactionOptional.get();
                cacheManager.addTransaction(transaction);
                Response response = new Response();
                response.setCorrelationId(correlationId);
                response.setProfileId(transaction.getProfileId());
                response.setTransactionStatus(transaction.getTransactionStatus().name());
                setResponseMessage(response);
                return response;
            } else {
                logger.info("Transaction not found in database");
                Response response = new Response();
                response.setCorrelationId(correlationId);
                response.setTransactionStatus(TransactionStatus.UNKNOWN.name());
                setResponseMessage(response);
                return response;
            }
        }
        else {
            logger.info("Transaction found in cache");
            Transaction transaction = transactionOptional.get();
            Response response = new Response();
            response.setCorrelationId(correlationId);
            response.setProfileId(transaction.getProfileId());
            response.setTransactionStatus(transaction.getTransactionStatus().name());
            setResponseMessage(response);
            return response;
        }
    }

    @Override
    public Response getProfileDetails(String profileId) {
        logger.info("Fetching Profile from cache");
        Optional<Profile> profileOptional = cacheManager.getProfile(profileId);
        if(profileOptional.isPresent()) {
            logger.info("Profile details found in cache");
            Profile profile = profileOptional.get();
            Response response = new Response();
            response.setProfile(profile);
            response.setMessage("Fetched Profile details successfully");
            response.setProfileId(profileId);
            return response;
        } else {
            logger.info("Profile details not found in cache, trying to get from DB");
            profileOptional = profileDao.getProfileById(profileId);
            if(profileOptional.isPresent()) {
                logger.info("Profile details found in DB");
                Profile profile = profileOptional.get();
                Response response = new Response();
                response.setProfile(profile);
                response.setMessage("Fetched Profile details successfully");
                response.setProfileId(profileId);
                return response;
            }
            else {
                logger.info("Profile details not found in DB");
                Response response = new Response();
                response.setProfileId(profileId);
                response.setMessage(UNABLE_TO_FETCH_PROFILE);
                return response;
            }
        }
    }

    private void setResponseMessage(Response response) {
        switch (response.getTransactionStatus()) {
            case "ACCEPTED" : {
                response.setMessage(PROFILE_CREATED);
                break;
            }
            case "REJECTED" : {
                response.setMessage(PROFILE_REJECTED);
                break;
            }
            case "IN_PROGRESS" : {
                response.setMessage(PROFILE_IN_PROGRESS);
                break;
            }
            default: {
                response.setMessage(UNABLE_TO_FETCH_TRANSACTION);
            }
        }
    }
}
