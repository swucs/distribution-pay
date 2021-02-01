package com.paktitucci.distribution.pay.application.dto;

import com.paktitucci.distribution.pay.domain.dto.ReceivingRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class Receiving {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Request {
        private final String token;
        private final Long userId;
        private final String roomId;

        public ReceivingRequest toReceivingRequest() {
            return ReceivingRequest.builder()
                                .token(this.token)
                                .userId(this.userId)
                                .roomId(this.roomId)
                                .build();
        }
    }




    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Response {
        private final long amount;
    }
}
