package com.paktitucci.distribution.pay.domain.entity;

import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DistributedAmountEntityTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, -10, -12, -4, -2})
    @DisplayName("뿌리기 엔티티 생성할 때 금액이 0 이하인 경우 에러 테스트")
    public void distributedAmountInitErrorTestWhenAmountIsLessThatZero(long amount) {
        DistributedAmountEntity.DistributedAmountEntityBuilder distributedAmountEntityBuilder =
                DistributedAmountEntity.builder()
                                .amount(amount)
                                .ownerId(1L)
                                .roomId("room")
                                .token("pay")
                                .numbersOfMemberReceived(1);
        Assertions.assertThrows(DistributionException.class, () -> distributedAmountEntityBuilder.build());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, -10, -12, -4, -2})
    @DisplayName("뿌리기 엔티티 생성할 때 받는 사람 수가 0 이하인 경우 에러 테스트")
    public void distributedAmountInitErrorTestWhenNumbersOfMemberReceivedIsLessThatZero(int numbersOfMemberReceived) {
        DistributedAmountEntity.DistributedAmountEntityBuilder distributedAmountEntityBuilder =
                DistributedAmountEntity.builder()
                                .amount(10000)
                                .ownerId(1L)
                                .roomId("room")
                                .token("pay")
                                .numbersOfMemberReceived(numbersOfMemberReceived);
        Assertions.assertThrows(DistributionException.class, () -> distributedAmountEntityBuilder.build());
    }

    @ParameterizedTest
    @ValueSource(ints = {1000, 2400, 3000, 6100, 14300, 542134950})
    @DisplayName("뿌리기 로직 수행 후 각자 받은 것들을 합산한 것이 뿌린 금액과 동일한지 확인하는 테스트")
    public void distributeToMembersTest(long amount) {
        // given
        DistributedAmountEntity distributedAmountEntity = DistributedAmountEntity.builder()
                                                            .amount(amount)
                                                            .numbersOfMemberReceived(5)
                                                            .ownerId(0L)
                                                            .roomId("room")
                                                            .token("pay")
                                                            .build();
        // when
        distributedAmountEntity.distributeByNumbersOfMemberReceived();

        // then
        long totalAmount = distributedAmountEntity.getDistributedAmountDetailEntities()
                                            .stream()
                                            .mapToLong(DistributedAmountDetailEntity::getAmount)
                                            .sum();
        assertThat(totalAmount).isEqualTo(amount);
    }

    @ParameterizedTest
    @MethodSource("ownerTestParameterProvider")
    @DisplayName("받는 사람이 뿌린 사람과 동일한지 체크하는 테스트")
    public void isOwnerTest(Long ownerId, Long requestUserId, boolean exceptedValue) {
        DistributedAmountEntity distributedAmountEntity = DistributedAmountEntity.builder()
                                                            .amount(1000)
                                                            .numbersOfMemberReceived(2)
                                                            .ownerId(ownerId)
                                                            .roomId("room")
                                                            .token("pay")
                                                            .build();
        boolean owner = distributedAmountEntity.isOwner(requestUserId);
        assertThat(owner).isEqualTo(exceptedValue);
    }

    private static Stream<Arguments> ownerTestParameterProvider() {
        return Stream.of(
                Arguments.arguments(1L, 1L, Boolean.TRUE),
                Arguments.arguments(283741L, 283741L, Boolean.TRUE),
                Arguments.arguments(1L, 2L, Boolean.FALSE),
                Arguments.arguments(28467382L, 18264632L, Boolean.FALSE)
        );
    }

    @ParameterizedTest
    @MethodSource("roomTestParameterProvider")
    @DisplayName("동일한 방의 사용자가 요청한 것인지 체크하는 테스트")
    public void isSameRoomTest(String roomId, String requestRoomId, boolean expectedValue) {
        DistributedAmountEntity distributedAmountEntity = DistributedAmountEntity.builder()
                                                            .amount(1000)
                                                            .numbersOfMemberReceived(2)
                                                            .ownerId(1L)
                                                            .roomId(roomId)
                                                            .token("pay")
                                                            .build();
        boolean sameRoom = distributedAmountEntity.isRequestOfSameRoomUser(requestRoomId);
        assertThat(sameRoom).isEqualTo(expectedValue);
    }

    private static Stream<Arguments> roomTestParameterProvider() {
        return Stream.of(
                Arguments.arguments("room", "room", Boolean.TRUE),
                Arguments.arguments("hello", "hello", Boolean.TRUE),
                Arguments.arguments("oracle", "mysql", Boolean.FALSE),
                Arguments.arguments("java", "python", Boolean.FALSE)
        );
    }


    @Test
    @DisplayName("이미 받은 사용자인지 확인하는 테스트")
    public void isAlreadyReceivedTest() {
        // given
        DistributedAmountEntity distributedAmountEntity = getDistributedAmountParameter();
        distributedAmountEntity.getDistributedAmountDetailEntities().get(0).assignUser(128374L);

        // when
        boolean alreadyReceived = distributedAmountEntity.isAlreadyReceived(128374L);

        // then
        assertThat(alreadyReceived).isTrue();
    }

    @Test
    @DisplayName("아직 받지 않은 사용자인지 확인하는 테스트")
    public void isNotAlreadyReceivedTest() {
        // given
        DistributedAmountEntity distributedAmountEntity = getDistributedAmountParameter();

        // when
        boolean alreadyReceived = distributedAmountEntity.isAlreadyReceived(128374L);

        // then
        assertThat(alreadyReceived).isFalse();
    }

    private static DistributedAmountEntity getDistributedAmountParameter() {
        DistributedAmountEntity distributedAmountEntity = DistributedAmountEntity.builder()
                                                            .amount(10000)
                                                            .ownerId(1L)
                                                            .roomId("room")
                                                            .token("pay")
                                                            .numbersOfMemberReceived(3)
                                                            .build();
        List<DistributedAmountDetailEntity> distributedAmountDetailEntities = distributedAmountEntity.getDistributedAmountDetailEntities();
        DistributedAmountDetailEntity detail1 = DistributedAmountDetailEntity.builder().amount(1000L).build();
        DistributedAmountDetailEntity detail2 = DistributedAmountDetailEntity.builder().amount(4000L).build();
        DistributedAmountDetailEntity detail3 = DistributedAmountDetailEntity.builder().amount(6000L).build();
        distributedAmountDetailEntities.add(detail1);
        distributedAmountDetailEntities.add(detail2);
        distributedAmountDetailEntities.add(detail3);

        return distributedAmountEntity;
    }

    @Test
    @DisplayName("유효한 Token인지 확인하는 테스트")
    public void isValidTokenTest() {
        // given
        DistributedAmountEntity distributedAmountEntity = DistributedAmountEntity.builder()
                                                            .amount(10000L)
                                                            .numbersOfMemberReceived(5)
                                                            .ownerId(1L)
                                                            .roomId("room")
                                                            .token("pay")
                                                            .build();
        String requestToken = "pad";

        // when
        boolean validToken = distributedAmountEntity.isValidToken(requestToken);

        // then
        assertThat(validToken).isFalse();
    }
}
