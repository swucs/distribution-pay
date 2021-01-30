package com.paktitucci.distribution.pay.domain.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
public class DistributionResponse {
    private final String token;

    @Builder
    public DistributionResponse(String token) {
        this.token = token;
    }
}
