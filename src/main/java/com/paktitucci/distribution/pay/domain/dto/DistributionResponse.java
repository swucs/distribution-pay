package com.paktitucci.distribution.pay.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@AllArgsConstructor
@Builder
@Getter
public class DistributionResponse {
    private final String token;
}
