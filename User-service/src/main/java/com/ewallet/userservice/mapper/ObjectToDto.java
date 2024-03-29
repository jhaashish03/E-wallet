package com.ewallet.userservice.mapper;

import com.ewallet.userservice.dtos.UserDto;
import com.ewallet.userservice.entities.User;

public class ObjectToDto {


    public static UserDto userToUserDto(User user){
        return UserDto.builder().userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .kycId(user.getKycId()).build();
    }
}
