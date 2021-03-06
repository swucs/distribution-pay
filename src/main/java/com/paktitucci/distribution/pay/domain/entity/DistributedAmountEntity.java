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
        name="distributed_amount",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"token"}
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

    // ???????????? ?????? ??????
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

    // ?????? ?????? ???????????? ???????????? ??????
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

    // ????????? ????????? ?????? ?????? ???????????? ???????????? ??????
    public boolean isOwner(Long requestUserId) {
        boolean owner = this.ownerId.equals(requestUserId);
        log.info("[Check if it is the owner's request] owner = [{}], ownerId = [{}], requestUserId = [{}]",
                owner, ownerId, requestUserId);

        return owner;
    }

    // ????????? ?????? ?????? ???????????? ???????????? ??????
    public boolean isRequestOfSameRoomUser(String requestRoomId) {
        boolean requestOfSameRoomUser = this.roomId.equals(requestRoomId);
        log.info("[Check if the request is from the same room user] " +
                        "requestOfSameRoomUser = [{}], roomId = [{}], requestRoomId = [{}]",
                requestOfSameRoomUser, roomId, requestRoomId);

        return requestOfSameRoomUser;
    }

    // ????????? 10?????? ???????????? ???????????? ??????
    public boolean isAfterMoreThanMinutes(int minutes) {
        long secondsAfterDistribution = getDurationBetweenCreateDateAndNow().getSeconds();

        return secondsAfterDistribution >= minutes;

    }

    // ?????? ??? ????????? ??? ????????? ???????????? ???????????? ??????
    public boolean isValidToken(String requestToken) {
        return this.token.equals(requestToken);
    }

    // ?????? ??? ????????? ??? ???????????? ???????????? ???????????? ??????
    public boolean isAfterMoreThanDays(int days) {
        long daysAfterDistribution = getDurationBetweenCreateDateAndNow().toDays();

        return daysAfterDistribution > days;
    }

    // ?????? ??? ???????????? ?????????????????? ???????????? ??????
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
                .orElseThrow(() -> new DistributionException(ErrorCode.ALL_USER_RECEIVED));
    }

    private Duration getDurationBetweenCreateDateAndNow() {
        return Duration.between(createdDate, LocalDateTime.now());
    }

}
