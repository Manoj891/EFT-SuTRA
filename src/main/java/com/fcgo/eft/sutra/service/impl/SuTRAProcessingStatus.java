package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.entity.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.entity.NchlReconciled;
import com.fcgo.eft.sutra.repository.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.repository.NchlReconciledRepository;
import com.fcgo.eft.sutra.util.TransactionStatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuTRAProcessingStatus {
    private final NchlReconciledRepository reconciledRepository;
    private final TransactionStatusUpdate statusUpdate;
    private final EftBatchPaymentDetailRepository detailRepository;
    private final StringToJsonNode jsonNode;


}
