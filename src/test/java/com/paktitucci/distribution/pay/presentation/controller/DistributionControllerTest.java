package com.paktitucci.distribution.pay.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.application.dto.DistributionHistory;
import com.paktitucci.distribution.pay.application.service.DistributionService;
import com.paktitucci.distribution.pay.presentation.dto.DistributedAmountDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DistributionController.class)
public class DistributionControllerTest {

    private static final String X_USER_ID = "X-USER-ID";
    private static final String X_ROOM_ID = "X-ROOM-ID";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DistributionService distributionService;

    @Test
    public void distributeAmountTest() throws Exception {

        // given
        DistributedAmountDto.Request request = DistributedAmountDto.Request.builder()
                                                                .amount(10000L)
                                                                .numbersOfMemberReceived(3)
                                                                .build();
        Distribution.Response distributionResponse = Distribution.Response.builder()
                                                                        .token("dkt")
                                                                        .build();

        given(distributionService.distributeAmount(any())).willReturn(distributionResponse);

        // when
        ResultActions resultActions
                = mockMvc.perform(post("/amount-to-distribute-to-user")
                                    .accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header(X_USER_ID, 1L)
                                    .header(X_ROOM_ID, "room")
                                    .content(objectMapper.writeValueAsString(request)))
                                    .andDo(print());

        // then
        resultActions.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").value("dkt"))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }


    @Test
    public void getDistributedAmountHistoryTest() throws Exception {

        // given
        List<DistributionHistory.Response.ReceivedInfo> receivedInfos =
                List.of(
                        DistributionHistory.Response.ReceivedInfo.builder().userId(1L).amount(3000).build(),
                        DistributionHistory.Response.ReceivedInfo.builder().userId(2L).amount(2843).build()
                );


        DistributionHistory.Response distributionHistoryResponse =
                DistributionHistory.Response.builder()
                        .distributedDateTime(LocalDateTime.of(2021, 1, 27, 12, 3, 33))
                        .totalAmount(10000L)
                        .receivedAmount(5843L)
                        .receivedInfos(receivedInfos)
                        .build();

        when(distributionService.findDistributionHistory(any())).thenReturn(distributionHistoryResponse);

        // when
        ResultActions resultActions =
                mockMvc.perform(get("/distributed-history/{token}", "dkr")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(X_USER_ID, 1L)
                        .header(X_ROOM_ID, "room"))
                        .andDo(print());


        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(10000L))
                .andExpect(jsonPath("$.receivedAmount").value(5843L));
    }

}