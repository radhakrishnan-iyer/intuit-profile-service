package com.intuit.processor;

import com.intuit.cache.CacheManager;
import com.intuit.common.model.Request;
import com.intuit.common.model.mq.EventReply;
import com.intuit.common.model.profile.Profile;
import com.intuit.dao.IProfileDao;
import com.intuit.dao.ITransactionDao;
import com.intuit.dao.model.Transaction;
import com.intuit.dao.model.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class ResponseProcessorTest {
    private ResponseProcessor responseProcessor;
    private ITransactionDao transactionDao;
    private IProfileDao profileDao;
    private CacheManager cacheManager;
    private String insertTransactionQuery;
    private String upsertProfile;

    @BeforeEach
    public void setUp() {
        transactionDao = Mockito.mock(ITransactionDao.class);
        profileDao = Mockito.mock(IProfileDao.class);
        cacheManager = Mockito.mock(CacheManager.class);
        insertTransactionQuery = "insertTransactionQuery";
        upsertProfile = "upsertProfile";

        responseProcessor = new ResponseProcessor(transactionDao , profileDao, cacheManager, insertTransactionQuery, upsertProfile);
    }

    @Test
    public void testProcess() {
        Request request = new Request();
        Profile profile = new Profile();
        profile.setCompanyName("testCompany");
        request.setProfile(profile);
        request.setCorrelationId("corrId");

        EventReply eventReply = new EventReply();
        eventReply.setRequest(request);
        eventReply.setIsValid(true);
        eventReply.setMessage("Validated successfully");

        Transaction transaction = new Transaction();
        transaction.setCorrelationId("corrId");
        transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
        transaction.setVersionNo(1);

        Mockito.when(cacheManager.getTransaction(Mockito.eq(eventReply.getCorrelationId()))).thenReturn(Optional.of(transaction));
        Mockito.when(cacheManager.addProfile(Mockito.any(Profile.class))).thenReturn(true);
        Mockito.when(cacheManager.getTransaction(Mockito.any())).thenReturn(Optional.of(transaction));
        Mockito.when(transactionDao.insert(Mockito.any(Transaction.class) , Mockito.any(String.class))).thenReturn(true);
        Mockito.when(cacheManager.updateTransaction(Mockito.any(Transaction.class))).thenReturn(true);
        Mockito.when(profileDao.upsert(Mockito.any(Profile.class) ,Mockito.eq(upsertProfile))).thenReturn(true);
        responseProcessor.process(eventReply);
    }

    @Test
    public void testProcess_ignore_message_from_topic() {
        Request request = new Request();
        Profile profile = new Profile();
        profile.setCompanyName("testCompany");
        request.setProfile(profile);
        request.setCorrelationId("corrId");

        EventReply eventReply = new EventReply();
        eventReply.setRequest(request);
        eventReply.setIsValid(true);
        eventReply.setMessage("Validated successfully");

        Transaction transaction = new Transaction();
        transaction.setCorrelationId("corrId");
        transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
        transaction.setVersionNo(1);

        Mockito.when(cacheManager.getTransaction(Mockito.eq(eventReply.getCorrelationId()))).thenReturn(Optional.empty());
        responseProcessor.process(eventReply);
    }
}
