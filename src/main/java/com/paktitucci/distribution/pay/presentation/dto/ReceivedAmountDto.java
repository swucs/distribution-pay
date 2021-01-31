package com.paktitucci.distribution.pay.presentation.dto;

import com.paktitucci.distribution.pay.application.dto.Receiving;
import lombok.*;

public class ReceivedAmountDto {



    @Getter
    public static class Response {
        private long amount;

        public static Response from(Receiving.Response receivingResponse) {
            Response response = new Response();
            response.amount = receivingResponse.getAmount();

            return response;
        }
    }
}
