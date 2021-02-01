package com.paktitucci.distribution.pay.application.dto;


import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.utils.token.TokenGenerator;
import lombok.*;

public class Distribution {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Request {
        private static final int TOKEN_LENGTH = 3;

        private final Long userId;
        private final String roomId;
        private final long amount;
        private final int numbersOfMemberReceived;

        public DistributedAmountEntity toDistributedAmount() {
            return DistributedAmountEntity.builder()
                                    .amount(this.amount)
                                    .ownerId(this.userId)
                                    .numbersOfMemberReceived(this.numbersOfMemberReceived)
                                    .roomId(this.roomId)
                                    .token(TokenGenerator.generate(TOKEN_LENGTH))
                                    .build();
        }

    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Response {
        private final String token;
    }
}
