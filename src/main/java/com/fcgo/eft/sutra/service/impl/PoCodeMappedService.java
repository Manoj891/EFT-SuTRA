package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.oracle.PoCodeMapped;
import com.fcgo.eft.sutra.repository.oracle.PoCodeMappedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PoCodeMappedService {
    private final Map<Long, Integer> map = new HashMap<>();
    private final PoCodeMappedRepository poCodeMappedRepository;

    // poCodeMappedRepository.findAllByPoCode().forEach(poCode -> {
//        PoCodeMapped mapped = poCodeMappedRepository.findById(poCode).orElse(PoCodeMapped.builder().code(poCodeMappedRepository.findMaxCode()).poCode(poCode).build());
//        poCodeMappedRepository.save(mapped);
//    });
    public void setDate() {
        for (PoCodeMapped datum : poCodeMappedRepository.findAll()) {
            map.put(datum.getPoCode(), datum.getCode());
        }
    }

    public Integer getPoCode(long id) {
        return map.get(id);
    }
}
