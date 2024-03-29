package com.ewallet.userservice.exceptions;

import com.ewallet.userservice.entities.User;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
