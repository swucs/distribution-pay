package com.paktitucci.distribution.pay.domain.repository;

import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface DistributedAmountRepository extends JpaRepository<DistributedAmountEntity, Long> {

    Optional<DistributedAmountEntity> findByDistributedAmountId(long distributedAmountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DistributedAmountEntity> findByTokenAndRoomId(String token, String roomId);
}
