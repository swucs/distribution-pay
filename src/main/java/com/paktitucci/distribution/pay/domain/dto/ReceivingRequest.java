package com.paktitucci.distribution.pay.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ReceivingRequest {
    private final String token;
    private final Long userId;
    private final String roomId;
}
