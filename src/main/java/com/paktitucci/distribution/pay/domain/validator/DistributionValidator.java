package com.paktitucci.distribution.pay.domain.validator;

import com.paktitucci.distribution.pay.domain.code.ErrorCode;
import com.paktitucci.distribution.pay.domain.entity.DistributedAmountEntity;
import com.paktitucci.distribution.pay.domain.exception.DistributionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DistributionValidator {

    private static int VALID_SECONDS_TO_RECEIVE = 600;
    private static int VALID_DAYS_TO_FIND = 7;

    public void validateForReceiving(DistributedAmountEntity distributedAmountEntity, Long requestUserId, String roomId) {
        if(distributedAmountEntity.isAlreadyReceived(requestUserId)) {
            throw new DistributionException(ErrorCode.ALREADY_RECEIVED);
        }

        if(distributedAmountEntity.isOwner(requestUserId)) {
            throw new DistributionException(ErrorCode.OWNER_CANNOT_RECEIVE);
        }

        if(!distributedAmountEntity.isRequestOfSameRoomUser(roomId)) {
            throw new DistributionException(ErrorCode.REQUEST_FROM_ANOTHER_ROOM_USER);
        }

        if(distributedAmountEntity.isAfterMoreThanMinutes(VALID_SECONDS_TO_RECEIVE)) {
            throw new DistributionException(ErrorCode.EXPIRE_VALID_TIME_TO_RECEIVE);
        }

        if(distributedAmountEntity.assignedAllUser()) {
            throw new DistributionException(ErrorCode.ALL_USER_RECEIVED);
        }
    }

    public void validateGettingDistributionHistory(DistributedAmountEntity distributedAmountEntity, Long requestUserId,
                                                   String requestToken) {
        if(!distributedAmountEntity.isOwner(requestUserId)) {
            throw new DistributionException(ErrorCode.SEARCH_BY_NON_OWNER);
        }

        if(!distributedAmountEntity.isValidToken(requestToken)) {
            throw new DistributionException(ErrorCode.SEARCH_BY_INVALID_TOKEN);
        }

        if(distributedAmountEntity.isAfterMoreThanDays(VALID_DAYS_TO_FIND)) {
            throw new DistributionException(ErrorCode.EXPIRE_VALID_TIME_TO_SEARCH);
        }
    }
}
