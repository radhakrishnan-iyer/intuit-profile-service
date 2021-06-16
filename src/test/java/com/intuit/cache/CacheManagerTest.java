package com.intuit.cache;

import com.intuit.common.model.profile.Profile;
import com.intuit.dao.IProfileDao;
import com.intuit.dao.model.Transaction;
import com.intuit.dao.model.TransactionStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class CacheManagerTest {
    private CacheManager cacheManager;
    private ICacheService cacheService;
    private IProfileDao profileDao;

    @BeforeEach
    public void setUp() {
        cacheService = Mockito.mock(ICacheService.class);
        profileDao = Mockito.mock(IProfileDao.class);
        cacheManager = new CacheManager(cacheService,profileDao);
    }

    @Test
    public void testAddProfileSuccess() {
        try {
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            profile.setCompanyName("TestCompany");
            profile.setLegalName("Test");
            profile.setWebsite("abc@pqr.com");
            Mockito.when(cacheService.upsertProfileInCache(Mockito.eq(profile), Mockito.eq(OperationType.INSERT))).thenReturn(true);
            boolean result = cacheManager.addProfile(profile);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testAddProfileFailure() {
        try {
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            profile.setCompanyName("TestCompany");
            profile.setLegalName("Test");
            profile.setWebsite("abc@pqr.com");
            Mockito.when(cacheService.upsertProfileInCache(Mockito.eq(profile), Mockito.eq(OperationType.INSERT))).thenThrow(new Exception("Exception while adding profile to cache"));
            boolean result = cacheManager.addProfile(profile);
            Assertions.assertFalse(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testUpdateProfileSuccess() {
        try {
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            profile.setCompanyName("TestCompany");
            profile.setLegalName("Test");
            profile.setWebsite("abc@pqr.com");
            Mockito.when(cacheService.upsertProfileInCache(Mockito.eq(profile), Mockito.eq(OperationType.UPDATE))).thenReturn(true);
            boolean result = cacheManager.updateProfile(profile);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testUpdateProfileFailure() {
        try {
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            profile.setCompanyName("TestCompany");
            profile.setLegalName("Test");
            profile.setWebsite("abc@pqr.com");
            Mockito.when(cacheService.upsertProfileInCache(Mockito.eq(profile), Mockito.eq(OperationType.UPDATE))).thenThrow(new Exception("Exception while adding profile to cache"));
            boolean result = cacheManager.updateProfile(profile);
            Assertions.assertFalse(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testGetProfileSuccess() {
        try {
            String profileId = "profileId";
            Profile profile = new Profile();
            profile.setProfileId("profileId");
            profile.setCompanyName("TestCompany");
            profile.setLegalName("Test");
            profile.setWebsite("abc@pqr.com");
            Mockito.when(cacheService.getProfile(Mockito.eq(profileId))).thenReturn(Optional.of(profile));
            Optional<Profile> actualProfileOptional = cacheManager.getProfile(profileId);
            Assertions.assertEquals(profile , actualProfileOptional.get());
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testGetProfileFailure() {
        try {
            String profileId = "profileId";
            Mockito.when(cacheService.getProfile(Mockito.eq(profileId))).thenThrow(new Exception("Exception while adding profile to cache"));
            Optional<Profile> actualProfileOptional = cacheManager.getProfile(profileId);
            Assertions.assertFalse(actualProfileOptional.isPresent());
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testAddTransactionSuccess() {
        try {
            Transaction transaction = new Transaction();
            transaction.setCorrelationId("corrId");
            transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
            Mockito.when(cacheService.upsertTransactionInCache(Mockito.eq(transaction), Mockito.eq(OperationType.INSERT))).thenReturn(true);
            boolean result = cacheManager.addTransaction(transaction);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testAddTransactionFailure() {
        try {
            Transaction transaction = new Transaction();
            transaction.setCorrelationId("corrId");
            transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
            Mockito.when(cacheService.upsertTransactionInCache(Mockito.eq(transaction), Mockito.eq(OperationType.INSERT))).thenThrow(new Exception("Exception while adding profile to cache"));
            boolean result = cacheManager.addTransaction(transaction);
            Assertions.assertFalse(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testUpdateTransactionSuccess() {
        try {
            Transaction transaction = new Transaction();
            transaction.setCorrelationId("corrId");
            transaction.setTransactionStatus(TransactionStatus.ACCEPTED);
            Mockito.when(cacheService.upsertTransactionInCache(Mockito.eq(transaction), Mockito.eq(OperationType.UPDATE))).thenReturn(true);
            boolean result = cacheManager.updateTransaction(transaction);
            Assertions.assertTrue(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testUpdateTransactionFailure() {
        try {
            Transaction transaction = new Transaction();
            transaction.setCorrelationId("corrId");
            transaction.setTransactionStatus(TransactionStatus.ACCEPTED);
            Mockito.when(cacheService.upsertTransactionInCache(Mockito.eq(transaction), Mockito.eq(OperationType.UPDATE))).thenThrow(new Exception("Exception while adding profile to cache"));
            boolean result = cacheManager.updateTransaction(transaction);
            Assertions.assertFalse(result);
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testGetTransactionSuccess() {
        try {
            String corrId = "corrId";
            Transaction expectedTransaction = new Transaction();
            expectedTransaction.setCorrelationId(corrId);
            expectedTransaction.setTransactionStatus(TransactionStatus.ACCEPTED);

            Mockito.when(cacheService.getTransaction(Mockito.eq(corrId))).thenReturn(Optional.of(expectedTransaction));
            Optional<Transaction> actualTransactionOptional = cacheManager.getTransaction(corrId);
            Assertions.assertEquals(expectedTransaction , actualTransactionOptional.get());
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }

    @Test
    public void testGetTranscationFailure() {
        try {
            String corrId = "corrId";
            Mockito.when(cacheService.getTransaction(Mockito.eq(corrId))).thenThrow(new Exception("Exception while adding profile to cache"));
            Optional<Transaction> actualTransactionOptional = cacheManager.getTransaction(corrId);
            Assertions.assertFalse(actualTransactionOptional.isPresent());
        }
        catch (Exception ex) {
            Assertions.fail("Code should not reach here. Exeception thrown");
        }
    }
}
