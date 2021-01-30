package com.paktitucci.distribution.pay.application.service;


import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.domain.service.DistributedAmountService;
import com.paktitucci.distribution.pay.domain.validator.DistributionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DistributionService.class)
public class DistributionServiceTest {

    @Autowired
    private DistributionService distributionService;
    
    @MockBean
    private DistributedAmountService distributedAmountService;

    @Test
    @DisplayName("뿌리기 테스트")
    public void distributionTest() {
        // given
        Distribution.Request request = Distribution.Request.builder()
                .amount(10000L)
                .numbersOfMemberReceived(5)
                .roomId("room")
                .userId(1L)
                .build();
        // when
        Distribution.Response response = distributionService.distributeAmount(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken().length()).isEqualTo(3);
    }
}
