package com.paktitucci.distribution.pay.domain.repository;

import com.paktitucci.distribution.pay.domain.entity.DistributedAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface DistributedAmountRepository extends JpaRepository<DistributedAmount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DistributedAmount> findByTokenAndRoomId(String token, String roomId);
}
