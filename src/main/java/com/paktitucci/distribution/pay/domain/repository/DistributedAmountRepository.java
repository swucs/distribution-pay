package com.paktitucci.distribution.pay.domain.repository;

import com.paktitucci.distribution.pay.domain.entity.DistributedAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface DistributedAmountRepository extends JpaRepository<DistributedAmount, Long> {

    DistributedAmount findByDistributedAmountId(Long distributedAmountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    DistributedAmount findByTokenAndRoomId(String token, String roomId);
}
