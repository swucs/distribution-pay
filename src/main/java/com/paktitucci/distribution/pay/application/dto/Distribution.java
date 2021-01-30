package com.paktitucci.distribution.pay.application.dto;


import com.paktitucci.distribution.pay.domain.entity.DistributedAmount;
import com.paktitucci.distribution.pay.utils.token.TokenGenerator;
import lombok.*;

public class Distribution {

    @Getter
    public static class Request {
        private static final int TOKEN_LENGTH = 3;

        private final Long userId;
        private final String roomId;
        private final long amount;
        private final int numbersOfMemberReceived;

        @Builder
        public Request(Long userId, String roomId, long amount, int numbersOfMemberReceived) {
            this.userId = userId;
            this.roomId = roomId;
            this.amount = amount;
            this.numbersOfMemberReceived = numbersOfMemberReceived;
        }

        public DistributedAmount toDistributedAmount() {
            return DistributedAmount.builder()
                                    .amount(this.amount)
                                    .ownerId(this.userId)
                                    .numbersOfMemberReceived(this.numbersOfMemberReceived)
                                    .roomId(this.roomId)
                                    .token(TokenGenerator.generate(TOKEN_LENGTH))
                                    .build();
        }

    }

    @Getter
    public static class Response {
        private final String token;

        @Builder
        public Response(String token) {
            this.token = token;
        }
    }
}
