package com.paktitucci.distribution.pay.application.service;

import com.paktitucci.distribution.pay.application.dto.Receiving;
import com.paktitucci.distribution.pay.domain.dto.ReceivingRequest;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountDetailEntity;
import com.paktitucci.distribution.pay.domain.service.DistributedAmountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceivingService {
    private final DistributedAmountService distributedAmountService;

    public Receiving.Response receiveDistributedAmount(Receiving.Request request) {
        ReceivingRequest receivingRequest = request.toReceivingRequest();
        DistributedAmountDetailEntity detailAssignedToCurrentUser =
                distributedAmountService.receiveDistributedAmount(receivingRequest);

        return Receiving.Response.builder()
                                .amount(detailAssignedToCurrentUser.getAmount())
                                .build();
    }

}
