package com.intuit.dao;

import com.intuit.common.model.Request;
import com.intuit.dao.model.Transaction;
import com.intuit.dao.model.TransactionStatus;

public class DBUtil {

    public static Transaction createNewTransaction(Request request, String correlationId, String customerId) {
        Transaction transaction = new Transaction();
        transaction.setCorrelationId(correlationId);
        transaction.setCustomerId(customerId);
        transaction.setVersionNo(1);
        transaction.setInputRequestMessage(request.toString());
        transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
        return transaction;
    }
}
