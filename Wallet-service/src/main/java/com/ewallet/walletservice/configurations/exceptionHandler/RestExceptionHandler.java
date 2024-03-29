package com.ewallet.walletservice.configurations.exceptionHandler;

import com.ewallet.walletservice.entities.ErrorResponse;
import com.ewallet.walletservice.exceptions.InsufficientAmountException;
import com.razorpay.RazorpayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

    @ExceptionHandler(value = {InsufficientAmountException.class})
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(final InsufficientAmountException exception){
        return ResponseEntity.badRequest().body(ErrorResponse.builder().
                httpStatus(HttpStatus.BAD_REQUEST.value())
                .exception(InsufficientAmountException.class.getName())
                .message(exception.getMessage())
                .build());
    }
    @ExceptionHandler(value = {RazorpayException.class})
    public ResponseEntity<ErrorResponse> handleRazorpayException(final RazorpayException exception){
        return ResponseEntity.badRequest().body(ErrorResponse.builder().
                httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .exception(RazorpayException.class.getName())
                .message(exception.getMessage())
                .build());
    }
}
