package com.paktitucci.distribution.pay.domain.entity;

import com.paktitucci.distribution.pay.domain.code.ErrorCode;
import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "distributed_amount_detail")
@ToString
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DistributedAmountDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long distributedAmountDetailId;

    private long amount;

    private Long receivedUserId;

    private LocalDateTime receivedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distributed_amount_id")
    private DistributedAmountEntity distributedAmountEntity;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public DistributedAmountDetailEntity(long amount, DistributedAmountEntity distributedAmountEntity) {
        if (amount < 0) {
            log.error("Amount of members received is less than 0. amount = [{}]", amount);
            throw new DistributionException(ErrorCode.AMOUNT_IS_BATTER_THAN_ZERO);
        }

        this.amount = amount;
        this.distributedAmountEntity = distributedAmountEntity;
    }

    public void assignUser(Long requestUserId) {
        this.receivedUserId = requestUserId;
        this.receivedDateTime = LocalDateTime.now();
    }
}
