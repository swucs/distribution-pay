package com.paktitucci.distribution.pay.domain.entity;

import com.paktitucci.distribution.pay.domain.code.ErrorCode;
import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"token", "roomId"}
                )
        }
)
@ToString
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class DistributedAmountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long distributedAmountId;

    private long amount;

    private String roomId;

    private String token;

    private Long ownerId;

    private int numbersOfMemberReceived;

    @OneToMany(mappedBy = "distributedAmountEntity", cascade = CascadeType.ALL)
    private List<DistributedAmountDetailEntity> distributedAmountDetailEntities = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public DistributedAmountEntity(long amount, int numbersOfMemberReceived, @NonNull String token,
                                   @NonNull String roomId, @NonNull Long ownerId) {
        if (amount <= 0) {
            log.error("Amount for distribution is less than 0. amount = [{}]", amount);
            throw new DistributionException(ErrorCode.AMOUNT_IS_BATTER_THAN_ZERO);
        }

        if (numbersOfMemberReceived <= 0) {
            log.error("Numbers of member received is less than 0. numbersOfMemberReceived = [{}]", numbersOfMemberReceived);
            throw new DistributionException(ErrorCode.NUMBERS_OF_MEMBER_RECEIVED_IS_LESS_THAN_ZERO);
        }

        this.amount = amount;
        this.numbersOfMemberReceived = numbersOfMemberReceived;
        this.token = token;
        this.roomId = roomId;
        this.ownerId = ownerId;
    }

    // 분배하는 로직 수행
    public void distributeByNumbersOfMemberReceived() {
        long remainAmount = amount;
        for (int i = numbersOfMemberReceived; i > 0; i--) {
            if (i == 1) {
                addDistributedAmountDetails(remainAmount);
                break;
            }
            long currentAmount = (long) (Math.random() * (remainAmount / i));
            addDistributedAmountDetails(currentAmount);
            remainAmount -= currentAmount;
        }
    }

    private void addDistributedAmountDetails(long amount) {
        DistributedAmountDetailEntity amountDetail = DistributedAmountDetailEntity.builder()
                                                                    .amount(amount)
                                                                    .distributedAmountEntity(this)
                                                                    .build();
        distributedAmountDetailEntities.add(amountDetail);
    }

    // 이미 받은 사람인지 체크하는 로직
    public boolean isAlreadyReceived(Long requestUserId) {
        boolean alreadyReceived =
                distributedAmountDetailEntities.stream()
                        .map(distributedAmountDetailEntity -> distributedAmountDetailEntity.getReceivedUserId())
                        .filter(receivedUserId -> receivedUserId != null)
                        .anyMatch(receivedUserId -> receivedUserId.equals(requestUserId));
        log.info("[Check if you already received it] requestUserId = [{}], alreadyReceived = [{}]",
                requestUserId, alreadyReceived);

        return alreadyReceived;
    }

    // 요청한 사람이 뿌린 사람 본인인지 체크하는 로직
    public boolean isOwner(Long requestUserId) {
        boolean owner = this.ownerId.equals(requestUserId);
        log.info("[Check if it is the owner's request] owner = [{}], ownerId = [{}], requestUserId = [{}]",
                owner, ownerId, requestUserId);

        return owner;
    }

    // 동일한 방에 있는 사람인지 체크하는 로직
    public boolean isRequestOfSameRoomUser(String requestRoomId) {
        boolean requestOfSameRoomUser = this.roomId.equals(requestRoomId);
        log.info("[Check if the request is from the same room user] " +
                        "requestOfSameRoomUser = [{}], roomId = [{}], requestRoomId = [{}]",
                requestOfSameRoomUser, roomId, requestRoomId);

        return requestOfSameRoomUser;
    }

    // 뿌린지 10분이 지났는지 확인하는 로직
    public boolean isAfterMoreThanMinutes(int minutes) {
        long secondsAfterDistribution = getDurationBetweenCreateDateAndNow().getSeconds();

        return secondsAfterDistribution >= minutes;

    }

    // 뿌린 건 조회할 때 유효한 토큰인지 검증하는 로직
    public boolean isValidToken(String requestToken) {
        return this.token.equals(requestToken);
    }

    // 뿌린 건 조회할 때 일주일이 지났는지 확인하는 로직
    public boolean isAfterMoreThanDays(int days) {
        long daysAfterDistribution = getDurationBetweenCreateDateAndNow().toDays();

        return daysAfterDistribution > days;
    }

    // 뿌린 건 모두에게 할당되었는지 확인하는 로직
    public boolean assignedAllUser() {
        return distributedAmountDetailEntities.stream()
                .allMatch(detail -> detail.getReceivedUserId() != null &&
                        detail.getReceivedDateTime() != null);
    }

    public DistributedAmountDetailEntity getDistributedAmountDetailNoAssignedToUser() {
        return distributedAmountDetailEntities.stream()
                .filter(detail -> detail.getReceivedUserId() == null &&
                        detail.getReceivedDateTime() == null)
                .findFirst()
                .get();
    }

    private Duration getDurationBetweenCreateDateAndNow() {
        return Duration.between(createdDate, LocalDateTime.now());
    }

}
