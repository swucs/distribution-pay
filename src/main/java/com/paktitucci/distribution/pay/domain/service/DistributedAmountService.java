package com.paktitucci.distribution.pay.domain.service;


import com.paktitucci.distribution.pay.domain.entity.DistributedAmount;
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
    public DistributedAmount save(DistributedAmount distributedAmount) {
        distributedAmount.distributeToMembers();
        return distributedAmountRepository.save(distributedAmount);
    }

    @Transactional(readOnly = true)
    public Optional<DistributedAmount> findByTokenAndRoomId(String token, String roomId) {
        return distributedAmountRepository.findByTokenAndRoomId(token, roomId);
    }


}
