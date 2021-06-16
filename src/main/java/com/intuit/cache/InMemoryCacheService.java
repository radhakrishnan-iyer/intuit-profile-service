package com.intuit.cache;

import com.intuit.common.model.profile.Address;
import com.intuit.common.model.profile.Profile;
import com.intuit.dao.model.Transaction;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCacheService implements ICacheService {

    private final Map<String,Transaction> transactionCache;
    private final Map<String,Profile> profileCache;

    public InMemoryCacheService() {
        transactionCache = new ConcurrentHashMap<>();
        profileCache = new ConcurrentHashMap<>();
    }

    public InMemoryCacheService(Map<String,Transaction> transactionCache , Map<String,Profile> profileCache) {
        this.transactionCache = transactionCache;
        this.profileCache = profileCache;
    }

    @Override
    public boolean upsertTransactionInCache(Transaction transaction , OperationType operationType) throws Exception {
        switch (operationType) {
            case INSERT: {
                transactionCache.put(transaction.getCorrelationId() , transaction);
                return true;
            }
            case UPDATE: {
                //Entire merging could also be achieved using reflection
                Transaction currentTransaction = transactionCache.get(transaction.getCorrelationId());
                if(transaction.getVersionNo()>0) {
                    currentTransaction.setVersionNo(transaction.getVersionNo());
                }
                if(transaction.getInputRequestMessage()!=null) {
                    currentTransaction.setInputRequestMessage(transaction.getInputRequestMessage());
                }
                if(transaction.getTransactionStatus()!=null) {
                    currentTransaction.setTransactionStatus(transaction.getTransactionStatus());
                }
                if(transaction.getCustomerId()!=null) {
                    currentTransaction.setCustomerId(transaction.getCustomerId());
                }
                if(transaction.getProfileId()!=null) {
                    currentTransaction.setProfileId(transaction.getProfileId());
                }
                transactionCache.put(transaction.getCorrelationId() , currentTransaction);
                return true;
            }
            default: return false;
        }
    }

    @Override
    public boolean upsertProfileInCache(Profile profile , OperationType operationType) throws Exception {
        switch (operationType) {
            case INSERT: {
                profileCache.put(profile.getProfileId() , profile);
                return true;
            }
            case UPDATE: {
                //Entire merging could also be achieved using reflection
                Profile currentProfile = profileCache.get(profile.getProfileId());
                Address currentBusinessAddress = currentProfile.getBusinessAddress();
                Address currentLegalAddress = currentProfile.getLegalAddress();

                if(profile.getCompanyName()!=null) {
                    currentProfile.setCompanyName(profile.getCompanyName());
                }
                if(profile.getLegalName()!=null) {
                    currentProfile.setLegalName(profile.getLegalName());
                }
                if(profile.getEmail()!=null) {
                    currentProfile.setEmail(profile.getEmail());
                }
                if(profile.getTaxId()!=null) {
                    currentProfile.setTaxId(profile.getTaxId());
                }
                if(profile.getWebsite()!=null) {
                    currentProfile.setWebsite(profile.getWebsite());
                }

                Address newBusinessAddress = profile.getBusinessAddress();
                if(newBusinessAddress!=null) {
                    if(currentBusinessAddress==null) {
                        currentBusinessAddress = newBusinessAddress;
                    } else {
                        if (newBusinessAddress.getCity() != null) {
                            currentBusinessAddress.setCity(newBusinessAddress.getCity());
                        }
                        if (newBusinessAddress.getCountry() != null) {
                            currentBusinessAddress.setCountry(newBusinessAddress.getCountry());
                        }
                        if (newBusinessAddress.getLine1() != null) {
                            currentBusinessAddress.setLine1(newBusinessAddress.getLine1());
                        }
                        if (newBusinessAddress.getLine2() != null) {
                            currentBusinessAddress.setLine2(newBusinessAddress.getLine2());
                        }
                        if (newBusinessAddress.getState() != null) {
                            currentBusinessAddress.setState(newBusinessAddress.getState());
                        }
                        if (newBusinessAddress.getZip() != null) {
                            currentBusinessAddress.setZip(newBusinessAddress.getZip());
                        }
                    }
                }

                Address newLegalAddress = profile.getLegalAddress();
                if(newLegalAddress!=null) {
                    if(currentLegalAddress==null) {
                        currentLegalAddress = newLegalAddress;
                    } else {
                        if (newLegalAddress.getCity() != null) {
                            currentLegalAddress.setCity(newLegalAddress.getCity());
                        }
                        if (newBusinessAddress.getCountry() != null) {
                            currentLegalAddress.setCountry(newLegalAddress.getCountry());
                        }
                        if (newBusinessAddress.getLine1() != null) {
                            currentLegalAddress.setLine1(newLegalAddress.getLine1());
                        }
                        if (newBusinessAddress.getLine2() != null) {
                            currentLegalAddress.setLine2(newLegalAddress.getLine2());
                        }
                        if (newBusinessAddress.getState() != null) {
                            currentLegalAddress.setState(newLegalAddress.getState());
                        }
                        if (newBusinessAddress.getZip() != null) {
                            currentLegalAddress.setZip(newLegalAddress.getZip());
                        }
                    }
                }

                currentProfile.setBusinessAddress(currentBusinessAddress);
                currentProfile.setLegalAddress(currentLegalAddress);
                profileCache.put(profile.getProfileId() , currentProfile);
                return true;
            }
            default: return false;
        }
    }

    @Override
    public Optional<Transaction> getTransaction(String correlationId) throws Exception{
        return Optional.of(transactionCache.get(correlationId));
    }

    @Override
    public Optional<Profile> getProfile(String profileId) throws Exception {
        return Optional.of(profileCache.get(profileId));
    }
}
