package com.paktitucci.distribution.pay.application.dto;

import lombok.Builder;
import lombok.Getter;

public class Receiving {

    @Getter
    public static class Request {
        private final String token;
        private final Long userId;
        private final String roomId;

        @Builder
        public Request(String token, Long userId, String roomId) {
            this.token = token;
            this.userId = userId;
            this.roomId = roomId;
        }
    }


    @Getter
    public static class Response {
        private final long amount;

        @Builder
        public Response(long amount) {
            this.amount = amount;
        }
    }
}
