package com.ewallet.walletservice.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldError {

    private String field;
    private String errorCode;
    private String errorMessage;

}
