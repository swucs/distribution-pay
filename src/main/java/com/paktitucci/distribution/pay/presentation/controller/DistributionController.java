package com.paktitucci.distribution.pay.presentation.controller;


import com.paktitucci.distribution.pay.application.dto.Distribution;
import com.paktitucci.distribution.pay.application.dto.DistributionHistory;
import com.paktitucci.distribution.pay.application.service.DistributionService;
import com.paktitucci.distribution.pay.presentation.dto.DistributedAmountDto;
import com.paktitucci.distribution.pay.presentation.dto.DistributionHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DistributionController {

    private final DistributionService distributionService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/amount-to-distribute-to-user")
    public DistributedAmountDto.Response distributeAmount(
                                                @RequestHeader(value = "X-USER-ID") Long userId,
                                                @RequestHeader(value = "X-ROOM-ID") String roomId,
                                                @RequestBody DistributedAmountDto.Request distributedAmountDtoRequest) {
        Distribution.Request request = distributedAmountDtoRequest.toDistributionRequest(userId, roomId);
        Distribution.Response response = distributionService.distributeAmount(request);

        return DistributedAmountDto.Response.from(response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/distributed-history/{token}")
    public DistributionHistoryDto.Response getDistributionHistory(
                                                                @RequestHeader(value = "X-USER-ID") Long userId,
                                                                @RequestHeader(value = "X-ROOM-ID") String roomId,
                                                                @PathVariable String token) {
        DistributionHistory.Request distributionHistoryRequest = DistributionHistory.Request.builder()
                                                                                            .userId(userId)
                                                                                            .roomId(roomId)
                                                                                            .token(token)
                                                                                            .build();
        DistributionHistory.Response distributionHistoryResponse =
                distributionService.findDistributionHistory(distributionHistoryRequest);

        return DistributionHistoryDto.Response.from(distributionHistoryResponse);

    }
}
