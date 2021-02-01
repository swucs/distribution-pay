package com.paktitucci.distribution.pay.domain.service;


import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.domain.repository.DistributedAmountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistributedAmountService {

    private final DistributedAmountRepository distributedAmountRepository;

    @Transactional
    public DistributedAmountEntity save(DistributedAmountEntity distributedAmountEntity) {
        DistributedAmountEntity savedDistributedAmountEntity = distributedAmountRepository.save(distributedAmountEntity);
        savedDistributedAmountEntity.distributeByNumbersOfMemberReceived();
        return savedDistributedAmountEntity;
    }

    @Transactional(readOnly = true)
    public Optional<DistributedAmountEntity> findByTokenAndRoomId(String token, String roomId) {
        return distributedAmountRepository.findByTokenAndRoomId(token, roomId);
    }


}
