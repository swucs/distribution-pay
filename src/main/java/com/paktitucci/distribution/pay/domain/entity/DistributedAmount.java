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
                columnNames = {"token"}
            )
        }
    )
@ToString
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "distributedAmountId")
@EntityListeners(AuditingEntityListener.class)
public class DistributedAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long distributedAmountId;

    private long amount;

    private String roomId;

    private String token;

    private Long ownerId;

    private int numbersOfMemberReceived;

    @OneToMany(mappedBy = "distributedAmount", cascade = CascadeType.ALL)
    private List<DistributedAmountDetail> distributedAmountDetails = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public DistributedAmount(long amount, int numbersOfMemberReceived, @NonNull String token,
                             @NonNull String roomId, @NonNull Long ownerId) {
        if(amount <= 0) {
            log.error("Amount for distribution is less than 0. amount = [{}]", amount);
            throw new DistributionException(ErrorCode.AMOUNT_IS_BATTER_THAN_ZERO);
        }

        if(numbersOfMemberReceived <= 0) {
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
    public void distributeToMembers() {
        long remainAmount = amount;
        for(int i = numbersOfMemberReceived; i > 0; i--) {
            if(i == 1) {
                distributedAmountDetails.add(DistributedAmountDetail.builder()
                                                                    .amount(remainAmount)
                                                                    .build());
                break;
            }
            long currentAmount = (long) (Math.random() * (remainAmount / i));
            distributedAmountDetails.add(DistributedAmountDetail.builder()
                                                                .amount(currentAmount)
                                                                .build());
            remainAmount -= currentAmount;
        }
    }

    // 이미 받은 사람인지 체크하는 로직
    public boolean isAlreadyReceived(Long requestUserId) {
        boolean alreadyReceived =
                distributedAmountDetails.stream()
                                        .map(distributedAmountDetail -> distributedAmountDetail.getReceivedUserId())
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

    private Duration getDurationBetweenCreateDateAndNow() {
        return Duration.between(createdDate, LocalDateTime.now());
    }

}
