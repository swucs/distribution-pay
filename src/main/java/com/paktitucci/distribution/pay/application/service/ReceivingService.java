package com.paktitucci.distribution.pay.application.service;

import com.paktitucci.distribution.pay.application.dto.Receiving;
import com.paktitucci.distribution.pay.domain.code.ErrorCode;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmount;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountDetail;
import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import com.paktitucci.distribution.pay.domain.service.DistributedAmountService;
import com.paktitucci.distribution.pay.domain.validator.DistributionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReceivingService {
    private final DistributedAmountService distributedAmountService;
    private final DistributionValidator distributionValidator;

    @Transactional
    public Receiving.Response receiveDistributedAmount(Receiving.Request request) {
        DistributedAmount distributedAmount =
                distributedAmountService.findByTokenAndRoomId(request.getToken(), request.getRoomId())
                .orElseThrow(() -> new DistributionException(ErrorCode.NOT_EXIST_DISTRIBUTED_AMOUNT));
        distributionValidator.validateForReceiving(distributedAmount, request.getUserId(), request.getRoomId());

        DistributedAmountDetail detailNoAssignedToUser = distributedAmount.getDistributedAmountDetailNoAssignedToUser();
        detailNoAssignedToUser.assignUser(request.getUserId());

        return Receiving.Response.builder()
                                .amount(detailNoAssignedToUser.getAmount())
                                .build();
    }

}
