package com.paktitucci.distribution.pay.presentation.exception;


import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DistributionExceptionHandler {
    @ExceptionHandler(DistributionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleDistributionException(DistributionException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                                                .code(e.getCode())
                                                .message(e.getMessage())
                                                .build();
        log.error("[DistributionException] code = [{}], message = [{}]", e.getCode(), e.getMessage());

        return ResponseEntity.badRequest()
                            .body(errorResponse);
    }

}
