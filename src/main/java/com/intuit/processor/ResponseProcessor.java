package com.intuit.processor;

import com.intuit.cache.CacheManager;
import com.intuit.common.constant.Constants;
import com.intuit.common.model.mq.EventReply;
import com.intuit.common.model.profile.Profile;
import com.intuit.dao.DBUtil;
import com.intuit.dao.IProfileDao;
import com.intuit.dao.ITransactionDao;
import com.intuit.dao.model.Transaction;
import com.intuit.dao.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;


public class ResponseProcessor implements IResponseProcessor {
    private static Logger logger = LoggerFactory.getLogger(ResponseProcessor.class);

    private final ITransactionDao transactionDao;
    private final IProfileDao profileDao;
    private final CacheManager cacheManager;
    private final String insertTransactionQuery;
    private final String upsertProfile;

    public ResponseProcessor(ITransactionDao transactionDao , IProfileDao profileDao,  CacheManager cacheManager , String insertTransactionQuery , String upsertProfile) {
        this.transactionDao = transactionDao;
        this.profileDao = profileDao;
        this.cacheManager = cacheManager;
        this.insertTransactionQuery = insertTransactionQuery;
        this.upsertProfile = upsertProfile;
    }

    @Override
    public void process(EventReply eventReply) {
        MDC.clear();
        MDC.put(Constants.request_id , eventReply.getCorrelationId());
        MDC.put(Constants.customer_id , eventReply.getCustomerId());
        logger.info("Received response for {} with isValid : {} - message : {}" , eventReply.getCorrelationId() , eventReply.getIsValid() , eventReply.getMessage());

        Optional<Transaction> transactionOptional = cacheManager.getTransaction(eventReply.getCorrelationId());
        if(!transactionOptional.isPresent()) {
            logger.info("The event reply received from subscription service is supposed to be handled by other instance of profile service. Hence ignoring the event {}" , eventReply);
            return;
        }

        // lookup transaction and update its version and status
        Profile profile = eventReply.getRequest().getProfile();
        if(eventReply.getIsValid()) {
            if (profile.getProfileId() == null || profile.getProfileId().length() == 0) {
                profile.setProfileId(UUID.randomUUID().toString());
                cacheManager.addProfile(profile);
            } else {
                cacheManager.updateProfile(profile);
            }
        } else {
            logger.info("Not updating profile in cache as the validation has failed from one ore more products for the given profile create/update operation");
        }

        Transaction transaction = DBUtil.createNewTransaction(eventReply.getRequest(), eventReply.getCorrelationId(), eventReply.getCustomerId());
        enrichTransaction(eventReply , transaction , profile);

        transactionDao.insert(transaction , insertTransactionQuery);
        cacheManager.updateTransaction(transaction);

        // insert the profile into DB and cache
        if(eventReply.getIsValid()) {
            profileDao.upsert(profile, upsertProfile);
        } else {
            logger.info("Not inserting the profile details to DB as the validation has failed");
        }
    }

    private void enrichTransaction(EventReply eventReply, Transaction transaction , Profile profile) {
        Optional<Transaction> transactionOptional = cacheManager.getTransaction(eventReply.getCorrelationId());
        if(transactionOptional.isPresent()) {
            Transaction cachedTransaction = transactionOptional.get();
            transaction.setTransactionStatus(cachedTransaction.getTransactionStatus());
            transaction.setCustomerId(cachedTransaction.getCustomerId());
            transaction.setInputRequestMessage(cachedTransaction.getInputRequestMessage());
            transaction.setVersionNo(cachedTransaction.getVersionNo()+1);
            transaction.setCorrelationId(cachedTransaction.getCorrelationId());
            transaction.setProfileId(profile.getProfileId());
        }
        if(eventReply.getIsValid()) {
            transaction.setTransactionStatus(TransactionStatus.ACCEPTED);
            transaction.setProfileId(eventReply.getRequest().getProfile().getProfileId());
        }
        else {
            transaction.setTransactionStatus(TransactionStatus.REJECTED);
        }
    }
}
