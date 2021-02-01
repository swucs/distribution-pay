package com.paktitucci.distribution.pay.domain.repository;

import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface DistributedAmountRepository extends JpaRepository<DistributedAmountEntity, Long> {

    Optional<DistributedAmountEntity> findByToken(String token);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT D from DistributedAmountEntity D where D.token = :token")
    Optional<DistributedAmountEntity> findByTokenForUpdate(String token);
}
