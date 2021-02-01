package com.paktitucci.distribution.pay.application.service;

import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.application.dto.DistributionHistory;
import com.paktitucci.distribution.pay.domain.code.ErrorCode;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import com.paktitucci.distribution.pay.domain.service.DistributedAmountService;
import com.paktitucci.distribution.pay.domain.validator.DistributionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DistributionService {

    private final DistributedAmountService distributedAmountService;
    private final DistributionValidator distributionValidator;

    @Transactional
    public Distribution.Response distributeAmount(Distribution.Request request) {
        DistributedAmountEntity distributedAmountEntity = request.toDistributedAmount();

        distributedAmountService.save(distributedAmountEntity);

        return Distribution.Response.builder()
                                    .token(distributedAmountEntity.getToken())
                                    .build();
    }


    @Transactional(readOnly = true)
    public DistributionHistory.Response findDistributionHistory(DistributionHistory.Request request) {
        DistributedAmountEntity distributedAmountEntity =
                distributedAmountService.findByTokenAndRoomId(request.getToken(), request.getRoomId())
                        .orElseThrow(() -> new DistributionException(ErrorCode.NOT_EXIST_DISTRIBUTED_AMOUNT));
        distributionValidator.validateGettingDistributionHistory(distributedAmountEntity, request.getUserId(), request.getToken());

        return DistributionHistory.Response.from(distributedAmountEntity);

    }

}
