package com.paktitucci.distribution.pay.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    AMOUNT_IS_BATTER_THAN_ZERO(-1000, "뿌리기를 위한 금액은 0원보다 커야 합니다."),
    NUMBERS_OF_MEMBER_RECEIVED_IS_LESS_THAN_ZERO(-1001, "받을 사람은 1명 이상이어야 합니다."),
    EXPIRE_VALID_TIME_TO_RECEIVE(-1002, "뿌린지 10분이 지났기 때문에 받을 수 없습니다."),
    ALREADY_RECEIVED(-1003, "이미 받은 사용자입니다."),
    OWNER_CANNOT_RECEIVE(-1004, "뿌린 사람이 받을 수 없습니다."),
    REQUEST_FROM_ANOTHER_ROOM_USER(-1005, "해당 방 사람의 요청이 아닙니다."),
    SEARCH_BY_NON_OWNER(-1006, "뿌린 사람이 아니면 조회가 불가능합니다."),
    SEARCH_BY_INVALID_TOKEN(-1007, "유효하지 않은 토큰으로 조회하였습니다."),
    EXPIRE_VALID_TIME_TO_SEARCH(-1008, "조회할 수 있는 기간이 지났습니다.");

    private int code;
    private String message;
}
