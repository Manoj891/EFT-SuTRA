package com.fcgo.eft.sutra.service;


import com.fcgo.eft.sutra.entity.oracle.NchlToken;

public interface CacheRepo {
    void save(NchlToken nchlRefreshToken);

    NchlToken findByKey();
}
