package com.paktitucci.distribution.pay.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmount;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountDetail;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DistributionHistory {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime distributedDateTime;
    private final long totalAmount;
    private final long receivedAmount;
    private final List<ReceivedInfo> receivedInfos;

    public static DistributionHistory from(DistributedAmount distributedAmount) {
        ReceivedInfos receivedInfos = ReceivedInfos.from(distributedAmount.getDistributedAmountDetails());

        return DistributionHistory.builder()
                                .distributedDateTime(distributedAmount.getCreatedDate())
                                .totalAmount(distributedAmount.getAmount())
                                .receivedInfos(receivedInfos.receivedInfos)
                                .receivedAmount(distributedAmount.getAmount() - receivedInfos.getTotalReceivedAmount())
                                .build();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReceivedInfos {
        private final List<ReceivedInfo> receivedInfos;

        private static ReceivedInfos from(List<DistributedAmountDetail> distributedAmountDetails) {
            return new ReceivedInfos(distributedAmountDetails.stream()
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

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Request {
        private final String token;
        private final Long userId;
        private final String roomId;
    }
}
