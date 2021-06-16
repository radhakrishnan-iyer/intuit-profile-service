package com.intuit.processor;

import com.intuit.cache.CacheManager;
import com.intuit.common.model.Request;
import com.intuit.common.model.Response;
import com.intuit.common.model.profile.Profile;
import com.intuit.dao.IProfileDao;
import com.intuit.dao.ITransactionDao;
import com.intuit.dao.model.Transaction;
import com.intuit.dao.model.TransactionStatus;
import com.intuit.service.QueuePublishService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.intuit.common.constant.Constants.PROFILE_CREATED;
import static com.intuit.common.constant.Constants.PROFILE_IN_PROGRESS;

public class RequestProcessorTest {
    private RequestProcessor requestProcessor;
    private QueuePublishService queuePublishService;
    private ITransactionDao transactionDao;
    private IProfileDao profileDao;
    private CacheManager cacheManager;
    private String insertTransactionQuery;
    private String getTransactionDetailsQuery;

    @BeforeEach
    public void setUp() {
        queuePublishService = Mockito.mock(QueuePublishService.class);
        transactionDao = Mockito.mock(ITransactionDao.class);
        profileDao = Mockito.mock(IProfileDao.class);
        cacheManager = Mockito.mock(CacheManager.class);
        insertTransactionQuery = "insertTransactionQuery";
        getTransactionDetailsQuery = "getTransactionDetailsQuery";
        requestProcessor = new RequestProcessor(queuePublishService , transactionDao, profileDao, cacheManager, insertTransactionQuery, getTransactionDetailsQuery);
    }

    @Test
    public void testProcess() {
        Mockito.when(transactionDao.insert(Mockito.any(Transaction.class) , Mockito.any())).thenReturn(true);
        Mockito.when(cacheManager.addTransaction(Mockito.any(Transaction.class))).thenReturn(true);
        Mockito.when(queuePublishService.publish(Mockito.any(Request.class))).thenReturn(true);
        Request request = new Request();
        request.setCorrelationId("corrId");

        boolean result = requestProcessor.process(request , "corrId" , "customerId");
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetTransactionDetails() {
        Request request = new Request();
        request.setCorrelationId("corrId");
        Transaction transaction = new Transaction();
        transaction.setCorrelationId("corrId");
        transaction.setCustomerId("customerId");
        transaction.setInputRequestMessage("inputRequestMessage");
        transaction.setTransactionStatus(TransactionStatus.ACCEPTED);
        Mockito.when(cacheManager.getTransaction("corrId")).thenReturn(Optional.of(transaction));

        Response respone = requestProcessor.getTransactionDetails(request.getCorrelationId());
        Assertions.assertEquals(PROFILE_CREATED , respone.getMessage());
    }

    @Test
    public void testGetTransactionDetails_From_DB() {
        Request request = new Request();
        request.setCorrelationId("corrId");
        Transaction transaction = new Transaction();
        transaction.setCorrelationId("corrId");
        transaction.setCustomerId("customerId");
        transaction.setInputRequestMessage("inputRequestMessage");
        transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
        Mockito.when(cacheManager.getTransaction("corrId")).thenReturn(Optional.empty());
        Mockito.when(transactionDao.getLatestTransaction("corrId"  , getTransactionDetailsQuery)).thenReturn(transaction);

        Response respone = requestProcessor.getTransactionDetails(request.getCorrelationId());
        Assertions.assertEquals(PROFILE_IN_PROGRESS , respone.getMessage());
    }

    @Test
    public void testGetProfileDetails() {

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setCompanyName("TestCompany");
        Mockito.when(cacheManager.getProfile("profileId")).thenReturn(Optional.of(profile));

        Response respone = requestProcessor.getProfileDetails("profileId");
        Assertions.assertEquals(profile , respone.getProfile());
    }

    @Test
    public void testGetProfileDetails_From_DB() {
        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setCompanyName("TestCompany");
        Mockito.when(cacheManager.getProfile("profileId")).thenReturn(Optional.empty());
        Mockito.when(profileDao.getProfileById("profileId")).thenReturn(profile);


        Response respone = requestProcessor.getProfileDetails("profileId");

        Assertions.assertEquals(profile , respone.getProfile());
    }
}
