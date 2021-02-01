package com.paktitucci.distribution.pay.domain.service;

import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import com.paktitucci.distribution.pay.domain.validator.DistributionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@AutoConfigureDataJpa
@ComponentScan(basePackages = "com.paktitucci.distribution.pay.domain")
@EnableJpaRepositories(basePackages = "com.paktitucci.distribution.pay.domain.repository")
@EntityScan(basePackages = "com.paktitucci.distribution.pay.domain.entity")
@EnableJpaAuditing
@SpringBootTest(classes = {DistributedAmountService.class})
public class DistributedAmountEntityServiceTest {

    @Autowired
    private DistributedAmountService distributedAmountService;

    @Autowired
    private DistributionValidator distributionValidator;

    @Test
    @DisplayName("뿌리기 10분 이후 만료되었는지 체크하는 테스트")
    public void expireReceiveAfterTenMinutesTest() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime tenMinutesLater = currentDateTime.plusSeconds(600L);

        try(MockedStatic<LocalDateTime> time = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            time.when(LocalDateTime::now).thenReturn(currentDateTime);
            DistributedAmountEntity distributedAmountEntity = DistributedAmountEntity.builder()
                                                                .amount(10000L)
                                                                .ownerId(1L)
                                                                .roomId("room")
                                                                .numbersOfMemberReceived(3)
                                                                .token("agf")
                                                                .build();

            distributedAmountService.save(distributedAmountEntity);
            time.when(LocalDateTime::now).thenReturn(tenMinutesLater);
            assertThrows(DistributionException.class, () ->
                    distributionValidator.validateForReceiving(distributedAmountEntity, 2L, "room"));
        }
    }

    @Test
    @DisplayName("뿌리기 10분 전이기 때문에 유효성 체크 통과하는 테스트")
    public void notExpireReceiveBeforeTenMinutesTest() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime tenMinutesLater = currentDateTime.plusSeconds(100L);

        try(MockedStatic<LocalDateTime> time = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            time.when(LocalDateTime::now).thenReturn(currentDateTime);
            DistributedAmountEntity distributedAmountEntity = DistributedAmountEntity.builder()
                                                                .amount(10000L)
                                                                .ownerId(1L)
                                                                .roomId("room")
                                                                .numbersOfMemberReceived(3)
                                                                .token("dkt")
                                                                .build();

            distributedAmountService.save(distributedAmountEntity);
            time.when(LocalDateTime::now).thenReturn(tenMinutesLater);
            assertDoesNotThrow(() -> distributionValidator.validateForReceiving(distributedAmountEntity, 2L, "room"));
        }
    }
}
