package com.paktitucci.distribution.pay.application.dto;

import com.paktitucci.distribution.pay.domain.dto.DistributionHistoryRequest;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountDetailEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;



public class DistributionHistory {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Request {
        private final String token;
        private final Long userId;
        private final String roomId;

        public DistributionHistoryRequest toDistributionHistoryRequest() {
            return DistributionHistoryRequest.builder()
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
        private final LocalDateTime distributedDateTime;
        private final long totalAmount;
        private final long receivedAmount;
        private final List<ReceivedInfo> receivedInfos;

        public static Response from(DistributedAmountEntity distributedAmountEntity) {
            ReceivedInfos receivedInfos = ReceivedInfos.from(distributedAmountEntity.getDistributedAmountDetailEntities());

            return Response.builder()
                    .distributedDateTime(distributedAmountEntity.getCreatedDate())
                    .totalAmount(distributedAmountEntity.getAmount())
                    .receivedInfos(receivedInfos.receivedInfos)
                    .receivedAmount(distributedAmountEntity.getAmount() - receivedInfos.getTotalReceivedAmount())
                    .build();
        }

        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        private static class ReceivedInfos {
            private final List<ReceivedInfo> receivedInfos;

            private static ReceivedInfos from(List<DistributedAmountDetailEntity> distributedAmountDetailEntities) {
                return new ReceivedInfos(distributedAmountDetailEntities.stream()
                        .filter(detail -> detail.getReceivedUserId() != null)
                        .map(detail ->
                                ReceivedInfo.builder()
                                        .amount(detail.getAmount())
                                        .userId(detail.getReceivedUserId())
                                        .build())
                        .collect(toList()));
            }

            private long getTotalReceivedAmount() {
                return receivedInfos.stream()
                        .mapToLong(receivedInfo -> receivedInfo.amount)
                        .sum();
            }

        }

        @Getter
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        @Builder
        public static class ReceivedInfo {
            private final long amount;
            private final long userId;
        }
    }
}
