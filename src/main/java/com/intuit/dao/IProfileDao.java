package com.intuit.dao;

import com.intuit.common.model.profile.Profile;

import java.util.List;

public interface IProfileDao {
    boolean upsert(Profile profile , String upsertQuery);
    List<Profile> getAllProfiles();
    Profile getProfileById(String profileId);
}
