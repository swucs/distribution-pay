package com.paktitucci.distribution.pay.application.service;

import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.application.dto.DistributionHistory;
import com.paktitucci.distribution.pay.domain.dto.DistributionHistoryRequest;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.domain.service.DistributedAmountService;
import com.paktitucci.distribution.pay.domain.validator.DistributionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DistributionService {

    private final DistributedAmountService distributedAmountService;

    @Transactional
    public Distribution.Response distributeAmount(Distribution.Request request) {
        DistributedAmountEntity distributedAmountEntity = request.toDistributedAmount();
        distributedAmountService.save(distributedAmountEntity);

        return Distribution.Response.builder()
                                    .token(distributedAmountEntity.getToken())
                                    .build();
    }


    @Transactional(readOnly = true)
    public DistributionHistory.Response getDistributionHistory(DistributionHistory.Request request) {
        DistributionHistoryRequest distributionHistoryRequest = request.toDistributionHistoryRequest();
        DistributedAmountEntity distributedAmountEntity =
                distributedAmountService.getDistributionHistory(distributionHistoryRequest);

        return DistributionHistory.Response.from(distributedAmountEntity);

    }

}
