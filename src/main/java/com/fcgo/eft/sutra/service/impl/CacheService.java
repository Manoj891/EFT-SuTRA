package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.oracle.NchlToken;
import com.fcgo.eft.sutra.repository.oracle.NchlTokenRepository;
import com.fcgo.eft.sutra.service.CacheRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService implements CacheRepo {
    private NchlToken nchlRefreshToken;
    private final NchlTokenRepository nchlTokenRepository;


    @Override
    public void save(NchlToken dto) {
        dto.setId(1);
        nchlTokenRepository.saveAndFlush(dto);
        nchlRefreshToken = dto;
    }

    @Override
    public NchlToken findByKey() {
        if (nchlRefreshToken == null) {
            nchlRefreshToken = nchlTokenRepository.findById(1).orElse(null);
        }
        return nchlRefreshToken;
    }
}
