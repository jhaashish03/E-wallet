package com.ewallet.userservice.exceptions;

public class UserAlreadyExitsException extends RuntimeException{

    public UserAlreadyExitsException(){

    }
   public UserAlreadyExitsException(String s){
        super(s);
    }
}
