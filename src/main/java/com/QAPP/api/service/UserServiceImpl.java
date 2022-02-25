package com.QAPP.api.service;

import com.QAPP.api.models.Role;
import com.QAPP.api.models.User;
import com.QAPP.api.repo.RoleRepo;
import com.QAPP.api.repo.UserRepo;
import com.QAPP.api.utility.GenerateOTP;
import com.QAPP.api.utility.SendSMS;
import com.QAPP.api.utility.SmsPojo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final GenerateOTP generateOTP;

    @Override
    public User getUser(String phone_number) {
        return userRepo.findUsersByPhoneNumber(phone_number);
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        log.info(" this is the phone number {}", phoneNumber);
        User user = userRepo.findUsersByPhoneNumber(phoneNumber);
        if (user == null) {
            log.error("User not found n the database");
            throw new UsernameNotFoundException("user not found");
        } else {
            log.info(" User found in the database");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role-> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public User saveUser(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User user = userRepo.save(newUser);
        addRoleToUser(user.getPhoneNumber(), "USER");
        user.setActive("N");
        user = generateOTP.genaratePin(user, "User Registration Token");
        return user;
    }

    @Override
    public User updateUser(User userUpdate) {
        User user = userRepo.findUsersByPhoneNumber(userUpdate.getPhoneNumber());
        if (user != null) {
            user.setExpiry(userUpdate.getExpiry());
            user.setOtp(userUpdate.getOtp());
            user.setUsername(userUpdate.getUsername());
            user.setFirstname(userUpdate.getFirstname());
            user.setLastname(userUpdate.getLastname());
            user.setPassword(userUpdate.getPassword());
            userRepo.save(user);
            return user;
        }
        return null;
    }

    @Override
    public User changePassword(String phoneNumber, String password) {
        User user = getUser(phoneNumber);
        user.setPassword(passwordEncoder.encode(password));
        return userRepo.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public String login (String phone_number, String password) {
        User user = getUser(phone_number);
        if (user != null) {
          if (passwordEncoder.matches(password, user.getPassword())) {
             user =  generateOTP.genaratePin(user,  "LOGIN TOKEN");
             updateUser(user);
             return "success";
          }
        }
        return "failure";
    }

    @Override
    public User verifyOTP(User userUpdate, String action) {
        User user = null;
        String otp = userUpdate.getOtp();
        if (userUpdate.getPhoneNumber() != null) {
            user = userRepo.findUsersByPhoneNumber(userUpdate.getPhoneNumber());
        } else {
             user = userRepo.findUserByEmail(userUpdate.getEmail());
        }
        Date now = new Date(System.currentTimeMillis());

        if (!passwordEncoder.matches(otp, user.getOtp())) {
            log.info(" wrong OTP provided");
            user.setTokenResponse("wrong OTP entered");
            return user;
        }

        if (now.getTime() > user.getExpiry().getTime()) {
        log.info("OTP has expired");
        user.setTokenResponse("OTP has expired");
        return user;
        }

         if ("register".equalsIgnoreCase(action)) {
         user.setActive("Y");
         userRepo.save(user);
         }
        user.setTokenResponse("success");
        return user;
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String phoneNumber, String roleName) {
        User user = userRepo.findUsersByPhoneNumber(phoneNumber);
        Role role = roleRepo.findRoleByName(roleName);
        user.getRoles().add(role);
    }

}
