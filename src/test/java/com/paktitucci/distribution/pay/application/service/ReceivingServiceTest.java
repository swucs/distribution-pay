package com.paktitucci.distribution.pay.application.service;


import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.application.dto.Receiving;
import com.paktitucci.distribution.pay.domain.service.DistributedAmountService;
import com.paktitucci.distribution.pay.domain.validator.DistributionValidator;
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
public class ReceivingServiceTest {
    @Autowired
    private ReceivingService receivingService;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private DistributedAmountService distributedAmountService;

    @Autowired
    private DistributionValidator distributionValidator;

    @Test
    public void receivingTest() {

        // given
        Distribution.Request distributionRequest = Distribution.Request.builder()
                                                                    .userId(1L)
                                                                    .roomId("room")
                                                                    .amount(1000L)
                                                                    .numbersOfMemberReceived(5)
                                                                    .build();

        Distribution.Response distributionResponse = distributionService.distributeAmount(distributionRequest);

        Receiving.Request request = Receiving.Request.builder()
                                                    .userId(2L)
                                                    .roomId("room")
                                                    .token(distributionResponse.getToken())
                                                    .build();

        // when
        Receiving.Response response = receivingService.receiveDistributedAmount(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isGreaterThanOrEqualTo(0);
    }
}
