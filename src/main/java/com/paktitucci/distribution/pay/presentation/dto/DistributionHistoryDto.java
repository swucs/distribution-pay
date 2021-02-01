package com.paktitucci.distribution.pay.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.paktitucci.distribution.pay.application.dto.DistributionHistory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class DistributionHistoryDto {


    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Response {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime distributedDateTime;
        private long totalAmount;
        private long receivedAmount;
        private List<DistributionHistory.Response.ReceivedInfo> receivedInfos;

        public static Response from(DistributionHistory.Response distributionHistoryResponse) {
            return Response.builder()
                        .distributedDateTime(distributionHistoryResponse.getDistributedDateTime())
                        .totalAmount(distributionHistoryResponse.getTotalAmount())
                        .receivedAmount(distributionHistoryResponse.getReceivedAmount())
                        .receivedInfos(distributionHistoryResponse.getReceivedInfos())
                        .build();
        }
    }
}
