package com.paktitucci.distribution.pay.domain.exception;

import com.paktitucci.distribution.pay.domain.code.ErrorCode;
import lombok.Getter;

@Getter
public class DistributionException extends RuntimeException {
    private int code;

    public DistributionException() {
        super();
    }

    public DistributionException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}
