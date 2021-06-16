package com.intuit.config;

import com.intuit.dao.IProfileDao;
import com.intuit.dao.ITransactionDao;
import com.intuit.dao.ProfileDao;
import com.intuit.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    @Value("${datasource.driver:com.mysql.jdbc.Driver}")
    private String dataSourceDriver;

    @Value("${datasource.url:jdbc:mysql://localhost:3306/intuit}")
    private String datasourceUrl;

    @Value("${datasource.db.user:root}")
    private String datasourceUser;

    @Value("${datasource.db.password:password}")
    private String datasourcePass;

    @Value("${get.all.profiles}")
    private String getAllProfile;

    @Value("${get.profile.by.id}")
    private String getProfileById;

    @Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dataSourceDriver);
        dataSource.setUrl(datasourceUrl);
        dataSource.setUsername(datasourceUser);
        dataSource.setPassword(datasourcePass);
        return dataSource;
    }

    @Bean
    public JdbcTemplate mysqlJdbcTemplate() {
        JdbcTemplate mysqlJdbcTemplate = new JdbcTemplate(mysqlDataSource());
        return mysqlJdbcTemplate;
    }


    @Bean
    public ITransactionDao transactionDao() {
        TransactionDao transactionDao = new TransactionDao(mysqlJdbcTemplate());
        return transactionDao;
    }

    @Bean
    public IProfileDao profileDao() {
        ProfileDao profileDao = new ProfileDao(mysqlJdbcTemplate() , getProfileById, getProfileById);
        return profileDao;
    }
}
