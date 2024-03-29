package com.ewallet.userservice.controllers;

import com.ewallet.userservice.dtos.UserDto;
import com.ewallet.userservice.entities.User;
import com.ewallet.userservice.exceptions.UserNotFoundException;
import com.ewallet.userservice.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/user-service")
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/new-user")
    public ResponseEntity<Void> createNewUser(@RequestBody @Valid final UserDto userDto) throws JsonProcessingException {
       User user= userService.createNewUser(userDto);
       if(user==null) return ResponseEntity.internalServerError().build();

       return ResponseEntity.created(URI.create("/user/"+user.getUserName())).build();
    }

    @GetMapping("/user-profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestPart @Email @NotNull String userName) throws UserNotFoundException, JsonProcessingException {
        return ResponseEntity.ok(userService.getUserProfile(userName));
    }

}
