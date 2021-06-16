package com.intuit.cache;

import com.intuit.common.model.profile.Profile;
import com.intuit.dao.IProfileDao;
import com.intuit.dao.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;

public class CacheManager {
    private static Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private final ICacheService cacheService;
    private final IProfileDao profileDao;

    public CacheManager(ICacheService cacheService , final IProfileDao profileDao) {
        this.cacheService = cacheService;
        this.profileDao = profileDao;
    }

    public void loadCache() {
        // load everything on startup
        logger.info("Loading all profiles on startup");
        profileDao.getAllProfiles().stream().map(profile -> addProfile(profile)).collect(Collectors.toList());
    }

    public boolean addProfile(Profile profile) {
        logger.info("Adding new profile {} to the cache" , profile);
        try {
            boolean result = cacheService.upsertProfileInCache(profile , OperationType.INSERT);
            return result;
        }
        catch (Exception ex) {
            logger.error("Error while adding Profile" , ex);
            return false;
        }
    }

    public boolean updateProfile(Profile profile) {
        logger.info("Update new profile {} to the cache" , profile);
        try {
            boolean result = cacheService.upsertProfileInCache(profile , OperationType.UPDATE);
            return result;
        }
        catch (Exception ex) {
            logger.error("Error while updating Profile" , ex);
            return false;
        }
    }

    public Optional<Profile> getProfile(String profileId) {
        logger.info("Fetching profile for the given profileId {}" , profileId);
        try {
            return cacheService.getProfile(profileId);
        }
        catch (Exception ex) {
            logger.error("Error while getting the profile from cache" , ex);
            return Optional.empty();
        }
    }

    public boolean addTransaction(Transaction transaction) {
        logger.info("Adding new Transaction {} to the cache" , transaction);
        try {
            boolean result = cacheService.upsertTransactionInCache(transaction , OperationType.INSERT);
            return result;
        }
        catch (Exception ex) {
            logger.error("Error while adding Transaction" , ex);
            return false;
        }
    }

    public boolean updateTransaction(Transaction transaction) {
        logger.info("Update new Transaction {} to the cache" , transaction);
        try {
            boolean result = cacheService.upsertTransactionInCache(transaction , OperationType.UPDATE);
            return result;
        }
        catch (Exception ex) {
            logger.error("Error while updating Transaction" , ex);
            return false;
        }
    }

    public Optional<Transaction> getTransaction(String correlationId) {
        logger.info("Fetching Transaction for the given correlationId {}" , correlationId);
        try {
            return cacheService.getTransaction(correlationId);
        }
        catch (Exception ex) {
            logger.error("Error while getting the Transaction from cache" , ex);
            return Optional.empty();
        }
    }

}
