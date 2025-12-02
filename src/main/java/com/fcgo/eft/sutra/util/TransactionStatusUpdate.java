package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.entity.NchlReconciled;
import com.fcgo.eft.sutra.repository.NchlReconciledRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TransactionStatusUpdate {
    private final NchlReconciledRepository repository;

    private final StringToJsonNode jsonNode;
    @Getter
    private boolean started = false;

    public void statusUpdate() {
        started = true;
        long datetime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date()));
        List<NchlReconciled>  list=repository.findByPushed(datetime - 3000);
        started = false;
    }

}
