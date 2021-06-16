package com.intuit.dao;

import com.intuit.dao.model.Transaction;
import com.intuit.dao.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class TransactionRowMapper implements RowMapper<Transaction> {
    private static Logger logger = LoggerFactory.getLogger(TransactionRowMapper.class);


    @Override
    public Transaction mapRow(ResultSet resultSet , int rowNum) {
        try {
            Transaction transaction = new Transaction();
            transaction.setProfileId(resultSet.getString("profile_id"));
            transaction.setCustomerId(resultSet.getString("customer_id"));
            transaction.setCorrelationId(resultSet.getString("correlation_id"));
            String transactionStatus = resultSet.getString("transaction_status");
            transaction.setTransactionStatus(transactionStatus==null ? TransactionStatus.UNKNOWN : TransactionStatus.valueOf(transactionStatus));
            return transaction;
        }
        catch (Exception ex) {
            logger.error("Exception while trying to fetch Transaction" , ex);
            throw new RuntimeException(ex);
        }
    }
}
