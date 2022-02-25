package com.QAPP.api.controller;

import com.QAPP.api.models.Role;
import com.QAPP.api.models.User;
import com.QAPP.api.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
@Slf4j
public class userResource {
    private final UserService userService;

    @PostMapping("/logIn")
    public ResponseEntity<String> login(@RequestBody LoginParams loginParams) {
       String login =  userService.login(loginParams.getPhoneNumber(), loginParams.getPassword());
       return ResponseEntity.ok().body(login);
    }

    @PostMapping("/verifyOTP/{action}")
    public ResponseEntity<User> verifyOTP(@PathVariable String action,  @RequestBody User user) {
        User tokenResponse = userService.verifyOTP(user, action);
        return ResponseEntity.ok().body(tokenResponse);
    }

    @GetMapping("/user/{phoneNumber}")
    public ResponseEntity<User> getUser(@PathVariable  String phoneNumber) {
        User user = userService.getUser(phoneNumber);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole (@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/addToUser")
    public ResponseEntity<?> addRoleToUser (@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registerUser")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return ResponseEntity.ok().body(userService.saveUser(user));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<User> changePassword(@RequestBody User user) {
        return ResponseEntity.ok().body(userService.changePassword(user.getEmail(), user.getPassword()));
    }

    @GetMapping("/refreshToken")
    public void RefreshToken (HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if ( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException(" Refresh token is missing");
        }
    }

}

@Data
class LoginParams {
    private String phoneNumber;
    private String password;
    private String otp;
}


@Data
class  RoleToUserForm {
    private String username;
    private String roleName;
}


