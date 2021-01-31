package com.paktitucci.distribution.pay.presentation.dto;

import com.paktitucci.distribution.pay.application.dto.Distribution;
import lombok.*;


public class DistributedAmountDto {


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Request {
        private long amount;
        private int numbersOfMemberReceived;

        public Distribution.Request toDistributionRequest(Long userId, String roomId) {
            return Distribution.Request.builder()
                            .userId(userId)
                            .roomId(roomId)
                            .amount(amount)
                            .numbersOfMemberReceived(numbersOfMemberReceived)
                            .build();
        }
    }

    @Getter
    public static class Response {
        private String token;

        public static Response from(Distribution.Response distributionResponse) {
            Response response = new Response();
            response.token = distributionResponse.getToken();
            return response;
        }
    }
}
