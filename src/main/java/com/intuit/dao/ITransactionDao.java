package com.intuit.dao;

import com.intuit.dao.model.Transaction;

import java.util.Optional;

public interface ITransactionDao {
    boolean insert(Transaction transaction, String insertQuery);
    Optional<Transaction> getLatestTransaction(String requestId, String query);
}
