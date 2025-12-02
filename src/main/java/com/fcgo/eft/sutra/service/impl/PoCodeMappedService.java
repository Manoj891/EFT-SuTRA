package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.PoCodeMapped;
import com.fcgo.eft.sutra.repository.PoCodeMappedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PoCodeMappedService {
    private final Map<Long, Integer> map = new HashMap<>();
    private final PoCodeMappedRepository poCodeMappedRepository;

    public void setDate() {
        for (PoCodeMapped datum : poCodeMappedRepository.findAll()) {
            map.put(datum.getPoCode(), datum.getCode());
        }
    }

    public Integer getPoCode(long id) {
        return map.get(id);
    }

    public synchronized int savePoCode(Long poCode) {
        int code = poCodeMappedRepository.findMaxCode();
        poCodeMappedRepository.saveAndFlush(PoCodeMapped.builder().poCode(poCode).code(code).build());
        return code;
    }
}
