package com.QAPP.api.utility;

import com.QAPP.api.models.User;
import com.QAPP.api.service.EmailService;
import com.QAPP.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Random;

@Slf4j
@Service
public class GenerateOTP {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    public  synchronized User genaratePin(User user, String subject){
        Random passwdIdRandom = new Random();
        String otp = "";
        for (int a = 0; a < 8; a++) {
            int iResult = passwdIdRandom.nextInt(9);
            otp += String.valueOf(iResult);
        }

        user.setOtp(passwordEncoder.encode(otp));
        user.setExpiry(new Timestamp(System.currentTimeMillis() + 1 * 60 * 1000));
        String message = "Dear " + user.getFirstname() + ", \n\nYour OTP  is " + otp + " .\n"
                + "Kindly note that the token will be valid for 20 minutes only. \n\n"
                + "Please enter the token on the app in order to proceed further. \n\n"
                + "Always keep your tokens safe. \n\n"
                + "Regards, \nQAPP";
        String  smsBody = "Dear Customer, please use " + otp + " as your one time password (OTP). It expires after 1 minute";
        SmsPojo smsPojo = new SmsPojo(user.getPhoneNumber(), smsBody);
        log.info("  email message >>> {} ", message);
        try {
            emailService.sendMail(user.getEmail(), subject, message);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        try {
            SendSMS.sendSMS(smsPojo);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return user;
    }
}
