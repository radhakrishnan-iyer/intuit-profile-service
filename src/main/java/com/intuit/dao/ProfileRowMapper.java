package com.intuit.dao;

import com.intuit.common.model.profile.Address;
import com.intuit.common.model.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class ProfileRowMapper implements RowMapper<Profile> {
    private static Logger logger = LoggerFactory.getLogger(ProfileRowMapper.class);

    @Override
    public Profile mapRow(ResultSet resultSet , int rowNum) {
        try {
            Profile profile = new Profile();
            profile.setProfileId(resultSet.getString("profile_id"));
            profile.setCompanyName(resultSet.getString("company_name"));
            profile.setLegalName(resultSet.getString("legal_name"));
            profile.setWebsite(resultSet.getString("website"));
            profile.setEmail(resultSet.getString("email"));
            profile.setTaxId(resultSet.getString("tax_id"));

            Address businessAddress = new Address();
            businessAddress.setZip(resultSet.getString("business_address_zip"));
            businessAddress.setState(resultSet.getString("business_address_state"));
            businessAddress.setLine2(resultSet.getString("business_address_line2"));
            businessAddress.setLine1(resultSet.getString("business_address_line1"));
            businessAddress.setCountry(resultSet.getString("business_address_country"));
            businessAddress.setCity(resultSet.getString("business_address_city"));

            Address legalAddress = new Address();
            legalAddress.setZip(resultSet.getString("legal_address_zip"));
            legalAddress.setState(resultSet.getString("legal_address_state"));
            legalAddress.setLine2(resultSet.getString("legal_address_line2"));
            legalAddress.setLine1(resultSet.getString("legal_address_line1"));
            legalAddress.setCountry(resultSet.getString("legal_address_country"));
            legalAddress.setCity(resultSet.getString("legal_address_city"));

            profile.setBusinessAddress(businessAddress);
            profile.setLegalAddress(legalAddress);

            return profile;
        }
        catch (Exception ex) {
            logger.error("Exception while mapping profile details fetched from DB to an Object" , ex);
            throw new RuntimeException(ex);
        }
    }
}
