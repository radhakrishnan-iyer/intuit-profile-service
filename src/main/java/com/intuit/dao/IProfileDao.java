package com.intuit.dao;

import com.intuit.common.model.profile.Profile;

import java.util.List;
import java.util.Optional;

public interface IProfileDao {
    boolean upsert(Profile profile , String upsertQuery);
    List<Profile> getAllProfiles();
    Optional<Profile> getProfileById(String profileId);
}
