package com.QAPP.api.repo;

import com.QAPP.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String>{
    User findUsersByPhoneNumber(String phoneNumber);
    User findUserByEmail(String email);
}
