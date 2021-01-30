package com.paktitucci.distribution.pay.application.service;

import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmount;
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
        DistributedAmount distributedAmount = request.toDistributedAmount();

        distributedAmountService.save(distributedAmount);

        return Distribution.Response.builder()
                                    .token(distributedAmount.getToken())
                                    .build();
    }

}
