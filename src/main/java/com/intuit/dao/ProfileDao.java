package com.intuit.dao;

import com.intuit.common.model.profile.Address;
import com.intuit.common.model.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class ProfileDao implements IProfileDao {
    private static Logger logger = LoggerFactory.getLogger(ProfileDao.class);

    private final JdbcTemplate mysqlJdbcTemplate;
    private final String getAllProfile;
    private final String getProfileById;

    public ProfileDao(JdbcTemplate mysqlJdbcTemplate, String getAllProfile, String getProfileById) {
        this.mysqlJdbcTemplate = mysqlJdbcTemplate;
        this.getAllProfile = getAllProfile;
        this.getProfileById = getProfileById;
    }

    @Override
    public boolean upsert(Profile profile, String upsertQuery) {
        logger.info("Upserting the Profile in DB - {}" , profile);
        Address businessAddress = profile.getBusinessAddress();
        Address legalAddress = profile.getLegalAddress();

        String profileId = profile.getProfileId();
        String email = profile.getEmail();
        String website = profile.getWebsite();
        String companyName = profile.getCompanyName();
        String legalName = profile.getLegalName();
        String taxId = profile.getTaxId();

        String businessAddressZip = businessAddress == null ? null : businessAddress.getZip();
        String businessAddressCity = businessAddress ==null ? null : businessAddress.getCity();
        String businessAddressCountry = businessAddress ==null ? null :  businessAddress.getCountry();
        String businessAddressState = businessAddress ==null ? null : businessAddress.getState();
        String businessAddressLine1 = businessAddress ==null ? null : businessAddress.getLine1();
        String businessAddressLine2 = businessAddress ==null ? null : businessAddress.getLine2();

        String legalAddressZip = legalAddress ==null ? null : legalAddress.getZip();
        String legalAddressCity = legalAddress ==null ? null : legalAddress.getCity();
        String legalAddressCountry = legalAddress ==null ? null : legalAddress.getCountry();
        String legalAddressState = legalAddress ==null ? null : legalAddress.getState();
        String legalAddressLine1 = legalAddress ==null ? null : legalAddress.getLine1();
        String legalAddressLine2 = legalAddress ==null ? null : legalAddress.getLine2();

        Object[] params = new Object[]{
                profileId,
                companyName,
                legalName,
                taxId,
                email,
                website,
                businessAddressLine1,
                businessAddressLine2,
                businessAddressCity,
                businessAddressState,
                businessAddressZip,
                businessAddressCountry,
                legalAddressLine1,
                legalAddressLine2,
                legalAddressCity,
                legalAddressState,
                legalAddressZip,
                legalAddressCountry,
                companyName,
                legalName,
                taxId,
                email,
                website,
                businessAddressLine1,
                businessAddressLine2,
                businessAddressCity,
                businessAddressState,
                businessAddressZip,
                businessAddressCountry,
                legalAddressLine1,
                legalAddressLine2,
                legalAddressCity,
                legalAddressState,
                legalAddressZip,
                legalAddressCountry
        };
        int rows = mysqlJdbcTemplate.update(upsertQuery , params);
        if(rows==1) {
            logger.info("Profile id {} upserted successfully in DB" , profileId);
            return true;
        }
        logger.error("Profile id {} upsert opration failed - profile {}" , profileId , profile);
        return false;
    }

    @Override
    public List<Profile> getAllProfiles() {
        try {
            List<Profile> profileList = mysqlJdbcTemplate.query(getAllProfile, new ProfileRowMapper());
            logger.info("Fetched all profiles from database. Size :{}" , profileList.size());
            return profileList;
        }
        catch (Exception ex) {
            logger.error("Exception while loading profile cache on startup");
            return new ArrayList<>();
        }
    }

    @Override
    public Profile getProfileById(String profileId) {
        try {
            List<Profile> profile = mysqlJdbcTemplate.query(getProfileById, new Object[] {profileId} , new ProfileRowMapper());
            logger.info("Fetched profile from database using id ");
            if(profile.size()==1)
                return profile.get(0);
            return new Profile();
        }
        catch (Exception ex) {
            logger.error("Exception while fetching profile from DB");
            return new Profile();
        }
    }
}
