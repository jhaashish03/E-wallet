package com.ewallet.userservice.configurations.exceptionHandler;

import com.ewallet.userservice.entities.ErrorResponse;
import com.ewallet.userservice.entities.FieldError;
import com.ewallet.userservice.exceptions.UserAlreadyExitsException;
import com.ewallet.userservice.exceptions.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

@ExceptionHandler(value = {UserAlreadyExitsException.class})
public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(final UserAlreadyExitsException exception){
return ResponseEntity.badRequest().body(ErrorResponse.builder().
        httpStatus(HttpStatus.BAD_REQUEST.value())
        .exception(UserAlreadyExitsException.class.getName())
        .message(exception.getMessage())
        .build());
}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception) {
        final BindingResult bindingResult = exception.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> {
                    final FieldError fieldError = new FieldError();
                    fieldError.setErrorCode(error.getCode());
                    fieldError.setField(error.getField());
                    fieldError.setErrorMessage(error.getDefaultMessage());
                    return fieldError;
                })
                .toList();
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setException(exception.getClass().getSimpleName());
        errorResponse.setFieldErrors(fieldErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = {JsonProcessingException.class})
    public ResponseEntity<ErrorResponse> handleServerSideRequestProcessingException(final Exception exception){
        return ResponseEntity.badRequest().body(ErrorResponse.builder().
                httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .exception(exception.getClass().getSimpleName())
                .message(exception.getMessage())
                .build());
    }
    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(final Exception exception){
        return ResponseEntity.badRequest().body(ErrorResponse.builder().
                httpStatus(HttpStatus.NOT_FOUND.value())
                .exception(exception.getClass().getSimpleName())
                .message(exception.getMessage())
                .build());
    }

}
