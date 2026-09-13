package com.prash.razorPay.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> jwtHandler(Exception e){
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                ErrorResponse.of("403","Unauthorized")
//        );
//    }


    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse>handleDuplicateResources(DuplicateResourceException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(ex.getErrorCode(),ex.getMessage())
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse>handleNotFount(ResourceNotFoundException ex){
    String errorCode=ex.getResourceName()+ " resource not found" + "_NOT_FOUND";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(errorCode,ex.getMessage())
        );

    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public  ResponseEntity<ErrorResponse>invalidState(InvalidStateTransitionException ex){

        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of("INVALID_STATUS","Cannot convert from the given state")
        );
    }
}
