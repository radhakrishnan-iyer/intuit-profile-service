package com.intuit.dao;

import com.intuit.dao.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

public class TransactionDao implements ITransactionDao {
    private static Logger logger = LoggerFactory.getLogger(TransactionDao.class);

    private final JdbcTemplate mysqlJdbcTemplate;

    public TransactionDao(JdbcTemplate mysqlJdbcTemplate) {
        this.mysqlJdbcTemplate = mysqlJdbcTemplate;
    }

    @Override
    public boolean insert(Transaction transaction , String insertQuery) {
        logger.info("Inserting Transaction details into Data-store {}" , transaction);
        Object[] params = new Object[] {transaction.getCorrelationId() , transaction.getVersionNo(), transaction.getCustomerId(),
                        transaction.getTransactionStatus().name() , transaction.getInputRequestMessage() , transaction.getProfileId()};
        int rows = mysqlJdbcTemplate.update(insertQuery , params);
        return rows>0;
    }

    @Override
    public Optional<Transaction> getLatestTransaction(String requestId, String query) {
        try {
            Object[] params = new Object[] {requestId , requestId};
            List<Transaction> transaction = mysqlJdbcTemplate.query(query, params, new TransactionRowMapper());
            if(transaction.size()>0) {
                return Optional.of(transaction.get(0));
            }
            return Optional.empty();
        }
        catch (Exception ex) {
            logger.error("Exception while getting the status of giving request id {}. Please retry" , requestId , ex);
            return Optional.empty();
        }
    }
}
