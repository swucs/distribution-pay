package com.paktitucci.distribution.pay.presentation.controller;


import com.paktitucci.distribution.pay.application.dto.Receiving;
import com.paktitucci.distribution.pay.application.service.ReceivingService;
import com.paktitucci.distribution.pay.presentation.dto.ReceivedAmountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReceivingController {

    private final ReceivingService receivingService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/distributed-amount/{token}")
    public ReceivedAmountDto.Response getDistributedAmount(
                                    @RequestHeader(value = "X-USER-ID") Long userId,
                                    @RequestHeader(value = "X-ROOM-ID") String roomId,
                                    @PathVariable String token) {
        Receiving.Request receivingRequest = Receiving.Request.builder()
                                                            .userId(userId)
                                                            .roomId(roomId)
                                                            .token(token)
                                                            .build();
        Receiving.Response receivingResponse = receivingService.receiveDistributedAmount(receivingRequest);

        return ReceivedAmountDto.Response.from(receivingResponse);
    }
}
