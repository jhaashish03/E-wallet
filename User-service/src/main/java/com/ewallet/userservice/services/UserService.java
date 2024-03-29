package com.ewallet.userservice.services;

import com.ewallet.userservice.dtos.UserDto;
import com.ewallet.userservice.entities.User;
import com.ewallet.userservice.exceptions.UserAlreadyExitsException;
import com.ewallet.userservice.exceptions.UserNotFoundException;
import com.ewallet.userservice.mapper.ObjectToDto;
import com.ewallet.userservice.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final KafkaTemplate<String,String> stringStringKafkaTemplate;

    @Autowired
    public UserService(UserRepository userRepository, StringRedisTemplate stringRedisTemplate, KafkaTemplate<String, String> stringStringKafkaTemplate) {
        this.userRepository = userRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.stringStringKafkaTemplate = stringStringKafkaTemplate;
    }

    @Transactional
    public User createNewUser(UserDto userDto) throws JsonProcessingException {
        if(userRepository.findByUserNameIgnoreCaseAllIgnoreCase(userDto.userName())!=null) throw new UserAlreadyExitsException("User already exists with username "+userDto.userName());

        User user=User.builder().firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .userName(userDto.userName())
                .kycId(userDto.kycId())
                .build();

        user=  userRepository.saveAndFlush(user);
        log.info("User created with username: {}",user.getUserName());
        setUserDetailInRedisCache(user);
        log.info("User cached in redis cache");
        publishUserCreationEventToKafka(user.getUserName());
        log.info("User creation event published to Kafka");
        return user;
    }

    public UserDto getUserProfile(String userName) throws JsonProcessingException, UserNotFoundException {
        User user=getUserFromCache(userName);
        if(user!=null){
            return ObjectToDto.userToUserDto(user);
        } else {
            user=userRepository.findByUserNameIgnoreCaseAllIgnoreCase(userName);
            if(user==null) throw new UserNotFoundException("User does not exist!");
            setUserDetailInRedisCache(user);
            return ObjectToDto.userToUserDto(user);
        }

    }


    private void setUserDetailInRedisCache(User user) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        String jsonForm=objectMapper.writeValueAsString(user);
        stringRedisTemplate.opsForValue().set(user.getUserName(),jsonForm,7, TimeUnit.DAYS);
    }
    private void publishUserCreationEventToKafka(String userName){
        stringStringKafkaTemplate.send("user_created",userName,userName);
    }

    private User getUserFromCache(String userName) throws JsonProcessingException {

        ObjectMapper objectMapper=new ObjectMapper();
       String stringValue= stringRedisTemplate.opsForValue().get(userName);
       if(stringValue==null) return null;
       return objectMapper.readValue(stringValue,User.class);
    }
}
