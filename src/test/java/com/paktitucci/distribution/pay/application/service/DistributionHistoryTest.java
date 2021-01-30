package com.paktitucci.distribution.pay.application.service;


import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.application.dto.DistributionHistory;
import com.paktitucci.distribution.pay.application.dto.Receiving;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureDataJpa
@ComponentScan(basePackages = "com.paktitucci.distribution.pay.domain")
@EnableJpaRepositories(basePackages = "com.paktitucci.distribution.pay.domain.repository")
@EntityScan(basePackages = "com.paktitucci.distribution.pay.domain.entity")
@EnableJpaAuditing
@SpringBootTest(classes = {ReceivingService.class, DistributionService.class})
public class DistributionHistoryTest {

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private ReceivingService receivingService;


    @Test
    @DisplayName("뿌리기 건의 현재 상태 조회하는 서비스 테스트")
    public void findDistributionHistoryTest() {
        Distribution.Request distributionRequest = Distribution.Request.builder()
                                                                    .userId(1L)
                                                                    .numbersOfMemberReceived(5)
                                                                    .amount(10000L)
                                                                    .roomId("room")
                                                                    .build();
        Distribution.Response distributionResponse = distributionService.distributeAmount(distributionRequest);


        Receiving.Request receivingRequest1 = Receiving.Request.builder()
                                                            .roomId("room")
                                                            .token(distributionResponse.getToken())
                                                            .userId(2L)
                                                            .build();

        Receiving.Response receivingResponse1 = receivingService.receiveDistributedAmount(receivingRequest1);

        Receiving.Request receivingRequest2 = Receiving.Request.builder()
                                                            .roomId("room")
                                                            .token(distributionResponse.getToken())
                                                            .userId(3L)
                                                            .build();
        Receiving.Response receivingResponse2 = receivingService.receiveDistributedAmount(receivingRequest2);


        DistributionHistory.Request distributionHistoryRequest =
                DistributionHistory.Request.builder()
                                        .roomId("room")
                                        .token(distributionResponse.getToken())
                                        .userId(1L)
                                        .build();

        DistributionHistory distributionHistory =
                distributionService.findDistributionHistory(distributionHistoryRequest);

        assertThat(distributionHistory).isNotNull();
        assertThat(distributionHistory.getReceivedAmount())
                .isEqualTo(distributionRequest.getAmount() -
                        receivingResponse1.getAmount() -
                        receivingResponse2.getAmount());
    }
}
