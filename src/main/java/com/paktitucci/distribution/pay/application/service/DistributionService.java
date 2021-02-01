package com.paktitucci.distribution.pay.application.service;

import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.application.dto.DistributionHistory;
import com.paktitucci.distribution.pay.domain.dto.DistributionHistoryRequest;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.domain.service.DistributedAmountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributionService {

    private final DistributedAmountService distributedAmountService;

    public Distribution.Response distributeAmount(Distribution.Request request) {
        DistributedAmountEntity distributedAmountEntity = request.toDistributedAmount();
        distributedAmountService.save(distributedAmountEntity);

        return Distribution.Response.builder()
                                    .token(distributedAmountEntity.getToken())
                                    .build();
    }


    public DistributionHistory.Response getDistributionHistory(DistributionHistory.Request request) {
        DistributionHistoryRequest distributionHistoryRequest = request.toDistributionHistoryRequest();
        DistributedAmountEntity distributedAmountEntity =
                distributedAmountService.getDistributionHistory(distributionHistoryRequest);

        return DistributionHistory.Response.from(distributedAmountEntity);
    }

}
