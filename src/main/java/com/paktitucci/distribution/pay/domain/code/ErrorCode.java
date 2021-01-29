package com.paktitucci.distribution.pay.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    AMOUNT_IS_BATTER_THAN_ZERO(-1000, "뿌리기를 위한 금액은 0원보다 커야 합니다."),
    NUMBERS_OF_MEMBER_RECEIVED_IS_LESS_THAN_ZERO(-1001, "받을 사람은 1명 이상이어야 합니다.");

    private int code;
    private String message;
}
