package com.intuit.cache;

import com.intuit.common.model.profile.Profile;
import com.intuit.dao.model.Transaction;

import java.util.Optional;

public interface ICacheService {
    boolean upsertTransactionInCache(Transaction transaction , OperationType operationType) throws Exception;
    boolean upsertProfileInCache(Profile profile , OperationType operationType) throws Exception;
    Optional<Transaction> getTransaction(String correlationId) throws Exception;;
    Optional<Profile> getProfile(String profileId) throws Exception;
}
