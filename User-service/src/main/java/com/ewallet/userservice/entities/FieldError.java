package com.ewallet.userservice.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldError {

    private String field;
    private String errorCode;
    private String errorMessage;

}
