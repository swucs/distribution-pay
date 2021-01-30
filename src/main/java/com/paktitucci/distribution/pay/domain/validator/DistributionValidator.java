package com.paktitucci.distribution.pay.domain.validator;

import com.paktitucci.distribution.pay.domain.code.ErrorCode;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmount;
import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DistributionValidator {

    private static int VALID_SECONDS_TO_RECEIVE = 600;
    private static int VALID_DAYS_TO_FIND = 7;

    public void validateForReceiving(DistributedAmount distributedAmount, Long requestUserId, String roomId) {
        if(distributedAmount.isAlreadyReceived(requestUserId)) {
            throw new DistributionException(ErrorCode.ALREADY_RECEIVED);
        }

        if(distributedAmount.isOwner(requestUserId)) {
            throw new DistributionException(ErrorCode.OWNER_CANNOT_RECEIVE);
        }

        if(!distributedAmount.isRequestOfSameRoomUser(roomId)) {
            throw new DistributionException(ErrorCode.REQUEST_FROM_ANOTHER_ROOM_USER);
        }

        if(distributedAmount.isAfterMoreThanMinutes(VALID_SECONDS_TO_RECEIVE)) {
            throw new DistributionException(ErrorCode.EXPIRE_VALID_TIME_TO_RECEIVE);
        }

        if(distributedAmount.assignedAllUser()) {
            throw new DistributionException(ErrorCode.ALL_USER_RECEIVED);
        }
    }

    public void validateGettingDistributionHistory(DistributedAmount distributedAmount, Long requestUserId,
                                                   String requestToken) {
        if(!distributedAmount.isOwner(requestUserId)) {
            throw new DistributionException(ErrorCode.SEARCH_BY_NON_OWNER);
        }

        if(!distributedAmount.isValidToken(requestToken)) {
            throw new DistributionException(ErrorCode.SEARCH_BY_INVALID_TOKEN);
        }

        if(distributedAmount.isAfterMoreThanDays(VALID_DAYS_TO_FIND)) {
            throw new DistributionException(ErrorCode.EXPIRE_VALID_TIME_TO_SEARCH);
        }
    }
}
