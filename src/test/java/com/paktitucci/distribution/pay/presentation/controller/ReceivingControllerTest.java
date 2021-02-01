package com.paktitucci.distribution.pay.presentation.controller;

import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.application.dto.DistributionHistory;
import com.paktitucci.distribution.pay.application.dto.Receiving;
import com.paktitucci.distribution.pay.application.service.DistributionService;
import com.paktitucci.distribution.pay.application.service.ReceivingService;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReceivingController.class)
public class ReceivingControllerTest {

    private static final String X_USER_ID = "X-USER-ID";
    private static final String X_ROOM_ID = "X-ROOM-ID";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReceivingService receivingService;
    @MockBean
    private DistributionService distributionService;


    @Test
    @DisplayName("뿌리기 받는 것 요청하는 테스트")
    public void receivingTest() throws Exception{

        // given
        Distribution.Response distributionResponse = Distribution.Response.builder()
                                                                        .token("dJR")
                                                                        .build();
        when(distributionService.distributeAmount(any())).thenReturn(distributionResponse);

        Receiving.Response receivingResponse = Receiving.Response.builder()
                                                                .amount(2560L)
                                                                .build();
        when(receivingService.receiveDistributedAmount(any())).thenReturn(receivingResponse);


        // when
        ResultActions resultActions = mockMvc.perform(patch("/distributed-amount/{token}", "dJR")
                                                    .accept(MediaType.APPLICATION_JSON_VALUE)
                                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                    .header(X_USER_ID, 2L)
                                                    .header(X_ROOM_ID, "room"))
                                             .andDo(print());


        //  then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(2560L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

}