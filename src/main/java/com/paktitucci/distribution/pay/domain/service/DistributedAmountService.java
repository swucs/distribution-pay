package com.paktitucci.distribution.pay.domain.service;


import com.paktitucci.distribution.pay.domain.code.ErrorCode;
import com.paktitucci.distribution.pay.domain.dto.DistributionHistoryRequest;
import com.paktitucci.distribution.pay.domain.dto.ReceivingRequest;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountDetailEntity;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import com.paktitucci.distribution.pay.domain.repository.DistributedAmountRepository;
import com.paktitucci.distribution.pay.domain.validator.DistributionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DistributedAmountService {

    private final DistributedAmountRepository distributedAmountRepository;
    private final DistributionValidator distributionValidator;

    @Transactional
    public void save(DistributedAmountEntity distributedAmountEntity) {
        distributedAmountEntity.distributeByNumbersOfMemberReceived();
        distributedAmountRepository.save(distributedAmountEntity);
    }

    @Transactional
    public DistributedAmountDetailEntity receiveDistributedAmount(ReceivingRequest request) {
        DistributedAmountEntity distributedAmountEntity =
                distributedAmountRepository.findByTokenForUpdate(request.getToken())
                        .orElseThrow(() -> new DistributionException(ErrorCode.NOT_EXIST_DISTRIBUTED_AMOUNT));
        distributionValidator.validateForReceiving(distributedAmountEntity, request.getUserId(), request.getRoomId());

        DistributedAmountDetailEntity detailNoAssignedToUser = distributedAmountEntity.getDistributedAmountDetailNoAssignedToUser();
        detailNoAssignedToUser.assignUser(request.getUserId());

        return detailNoAssignedToUser;
    }

    @Transactional(readOnly = true)
    public DistributedAmountEntity getDistributionHistory(DistributionHistoryRequest request) {
        DistributedAmountEntity distributedAmountEntity =
                distributedAmountRepository.findByToken(request.getToken())
                .orElseThrow(() -> new DistributionException(ErrorCode.NOT_EXIST_DISTRIBUTED_AMOUNT));
        distributionValidator.validateGettingDistributionHistory(distributedAmountEntity, request);
        return distributedAmountEntity;
    }


}
