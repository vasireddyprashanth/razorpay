package com.prash.razorPay.common.exception;

import com.prash.razorPay.common.enums.PaymentEvent;
import com.prash.razorPay.common.enums.PaymentStatus;

public class InvalidStateTransitionException extends RuntimeException{

    private String status;
    private String  event;
    public InvalidStateTransitionException(String status, String event){
        super("Invalid transition from " + status  +" to " + event);
        this.status=status;
        this.event=event ;

    }
}
