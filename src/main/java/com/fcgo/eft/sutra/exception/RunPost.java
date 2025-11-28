package com.fcgo.eft.sutra.exception;

import com.fcgo.eft.sutra.repository.oracle.RemoteIpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunPost {
    private final RemoteIpRepository remoteIpRepository;


//    @PostConstruct
    public void executePostConstruct() {


    }
}
