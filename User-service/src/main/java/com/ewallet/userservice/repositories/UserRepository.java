package com.ewallet.userservice.repositories;

import com.ewallet.userservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByUserNameIgnoreCaseAllIgnoreCase(String userName);
}
