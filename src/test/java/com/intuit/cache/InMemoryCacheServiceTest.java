package com.intuit.cache;

import com.intuit.common.model.profile.Address;
import com.intuit.common.model.profile.Profile;
import com.intuit.dao.model.Transaction;
import com.intuit.dao.model.TransactionStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class InMemoryCacheServiceTest {
    private static Logger logger = LoggerFactory.getLogger(InMemoryCacheServiceTest.class);

    private InMemoryCacheService inMemoryCacheService;
    private Map<String,Transaction> transactionCache;
    private Map<String, Profile> profileCache;

    @BeforeEach
    public void setUp() {
        transactionCache = Mockito.mock(Map.class);
        profileCache = Mockito.mock(Map.class);
        inMemoryCacheService = new InMemoryCacheService(transactionCache, profileCache);
    }

    @Test
    public void testInsertTransactionInCache() {
        try {
            Transaction transaction = new Transaction();
            transaction.setCorrelationId("corrId");
            Mockito.when(transactionCache.put(Mockito.eq(transaction.getCorrelationId()) , Mockito.eq(transaction))).thenReturn(transaction);
            boolean result = inMemoryCacheService.upsertTransactionInCache(transaction, OperationType.INSERT);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            logger.error("Exception ", ex);
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testUpdateTransactionInCache() {
        try {
            Transaction transaction = new Transaction();
            transaction.setCorrelationId("corrId");
            transaction.setVersionNo(2);
            transaction.setTransactionStatus(TransactionStatus.ACCEPTED);
            transaction.setInputRequestMessage("InputRequestMessage");
            transaction.setCustomerId("123");
            transaction.setProfileId("profileId");

            Transaction cachedTransaction = new Transaction();
            cachedTransaction.setCorrelationId("corrId");
            cachedTransaction.setProfileId("profileId");
            cachedTransaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
            cachedTransaction.setVersionNo(1);
            cachedTransaction.setCustomerId("123");
            cachedTransaction.setInputRequestMessage("InputRequestMessage");

            Mockito.when(transactionCache.get(Mockito.eq("corrId"))).thenReturn(cachedTransaction);
            Mockito.when(transactionCache.put(Mockito.eq("corrId") , Mockito.eq(cachedTransaction))).thenReturn(cachedTransaction);

            boolean result = inMemoryCacheService.upsertTransactionInCache(transaction, OperationType.UPDATE);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            logger.error("Exception ", ex);
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testGetTransaction() {
        try {
            Transaction transaction = new Transaction();
            transaction.setProfileId("profileId");
            transaction.setCorrelationId("corrId");
            Mockito.when(transactionCache.get(Mockito.eq("corrId"))).thenReturn(transaction);
            Optional<Transaction> expectedTransactionOptional = inMemoryCacheService.getTransaction("corrId");

            Assertions.assertEquals(transaction , expectedTransactionOptional.get());

        }
        catch (Exception ex) {
            logger.error("Exception ", ex);
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testInsertProfileInCache() {
        try {
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            Mockito.when(profileCache.put(Mockito.eq(profile.getProfileId()) , Mockito.eq(profile))).thenReturn(profile);
            boolean result = inMemoryCacheService.upsertProfileInCache(profile, OperationType.INSERT);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            logger.error("Exception ", ex);
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testUpdateProfileInCache() {
        try {
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            profile.setWebsite("xyz.com");
            profile.setLegalName("newLegalName");
            profile.setCompanyName("newCompanyName");
            profile.setEmail("abc@xyz.com");
            Address businessAddress = new Address();
            businessAddress.setCity("Mumbai");
            businessAddress.setCountry("India");
            businessAddress.setLine1("Line1");
            businessAddress.setLine2("Line2");
            businessAddress.setState("State");
            businessAddress.setZip("Zip");
            Address legalAddress = new Address();
            legalAddress.setCity("Mumbai");
            legalAddress.setCountry("India");
            legalAddress.setLine1("Line1");
            legalAddress.setLine2("Line2");
            legalAddress.setState("State");
            legalAddress.setZip("Zip");
            profile.setBusinessAddress(businessAddress);
            profile.setLegalAddress(legalAddress);


            Profile cachedProfile = new Profile();
            cachedProfile.setProfileId("profileId");
            cachedProfile.setWebsite("xyz.com");
            cachedProfile.setLegalName("newLegalName");
            cachedProfile.setCompanyName("newCompanyName");
            cachedProfile.setEmail("abc@xyz.com");
            Address cachedBusinessAddress = new Address();
            cachedBusinessAddress.setCity("Mumbai");
            cachedBusinessAddress.setCountry("India");
            cachedBusinessAddress.setLine1("Line1");
            cachedBusinessAddress.setLine2("Line2");
            cachedBusinessAddress.setState("State");
            cachedBusinessAddress.setZip("Zip");
            Address cachedLegalAddress = new Address();
            cachedLegalAddress.setCity("Mumbai");
            cachedLegalAddress.setCountry("India");
            cachedLegalAddress.setLine1("Line1");
            cachedLegalAddress.setLine2("Line2");
            cachedLegalAddress.setState("State");
            cachedLegalAddress.setZip("Zip");
            cachedProfile.setBusinessAddress(cachedBusinessAddress);
            cachedProfile.setLegalAddress(cachedLegalAddress);

            Mockito.when(profileCache.get(Mockito.eq("profileId"))).thenReturn(cachedProfile);
            Mockito.when(profileCache.put(Mockito.eq("profileId") , Mockito.eq(cachedProfile))).thenReturn(cachedProfile);

            boolean result = inMemoryCacheService.upsertProfileInCache(profile, OperationType.UPDATE);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            logger.error("Exception ", ex);
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testUpdateProfileInCache2() {
        try {
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            profile.setWebsite("xyz.com");
            profile.setLegalName("newLegalName");
            profile.setCompanyName("newCompanyName");
            profile.setEmail("abc@xyz.com");
            Address businessAddress = new Address();
            businessAddress.setCity("Mumbai");
            businessAddress.setCountry("India");
            businessAddress.setLine1("Line1");
            businessAddress.setLine2("Line2");
            businessAddress.setState("State");
            businessAddress.setZip("Zip");
            Address legalAddress = new Address();
            legalAddress.setCity("Mumbai");
            legalAddress.setCountry("India");
            legalAddress.setLine1("Line1");
            legalAddress.setLine2("Line2");
            legalAddress.setState("State");
            legalAddress.setZip("Zip");
            profile.setBusinessAddress(businessAddress);
            profile.setLegalAddress(legalAddress);


            Profile cachedProfile = new Profile();
            cachedProfile.setProfileId("profileId");
            cachedProfile.setWebsite("xyz.com");
            cachedProfile.setLegalName("newLegalName");
            cachedProfile.setCompanyName("newCompanyName");
            cachedProfile.setEmail("abc@xyz.com");

            Mockito.when(profileCache.get(Mockito.eq("profileId"))).thenReturn(cachedProfile);
            Mockito.when(profileCache.put(Mockito.eq("profileId") , Mockito.eq(cachedProfile))).thenReturn(cachedProfile);

            boolean result = inMemoryCacheService.upsertProfileInCache(profile, OperationType.UPDATE);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            logger.error("Exception ", ex);
            Assertions.fail("Code should not reach here");
        }
    }

    @Test
    public void testGetProfile() {
        try {
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            Mockito.when(profileCache.get(Mockito.eq("profileId"))).thenReturn(profile);
            Optional<Profile> expectedProfileOptional = inMemoryCacheService.getProfile("profileId");
            Assertions.assertEquals(profile , expectedProfileOptional.get());

        }
        catch (Exception ex) {
            logger.error("Exception ", ex);
            Assertions.fail("Code should not reach here");
        }
    }
}
