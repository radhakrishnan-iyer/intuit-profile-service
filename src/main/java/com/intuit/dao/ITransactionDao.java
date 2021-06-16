package com.intuit.dao;

import com.intuit.dao.model.Transaction;

public interface ITransactionDao {
    boolean insert(Transaction transaction, String insertQuery);
    Transaction getLatestTransaction(String requestId, String query);
}
