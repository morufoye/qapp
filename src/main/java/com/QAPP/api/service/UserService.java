package com.QAPP.api.service;

import com.QAPP.api.models.Role;
import com.QAPP.api.models.User;

import java.util.List;

public interface UserService {

    User getUser(String phoneNumber);
    User saveUser(User user);
    User updateUser(User user);
    User changePassword(String phoneNumber, String password);
    List<User> getUsers();
    String login(String phoneNumber, String password);
    User verifyOTP(User user, String action);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);

}
