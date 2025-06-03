package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.*;
import com.motherandbabymilk.dto.request.Login;
import com.motherandbabymilk.dto.request.Registration;
import com.motherandbabymilk.dto.response.RegistrationResponse;
import com.motherandbabymilk.dto.response.UpdateResponse;
import com.motherandbabymilk.dto.response.UserResponse;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.repository.UserRepository;
import com.motherandbabymilk.service.EmailService;
import com.motherandbabymilk.service.TokenService;
import com.motherandbabymilk.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(
        origins = {"http://localhost:5173"}
)
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody Registration register) {
        RegistrationResponse newUser = userService.register(register);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody Login login) {
        UserResponse checkuser = userService.login(login);
        return ResponseEntity.ok(checkuser);
        }

    @PostMapping("/logout")
    public String logout() {
        userService.logout();
        return "Logged out successfully";
    }

    @GetMapping({"/currentUser/detail"})
    public ResponseEntity<UpdateResponse> getCurrentUser() {
        UpdateResponse userResponse = this.userService.getCurrentUserInfo();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping({"/user"})
    public List getAllUsers() {
        return this.userService.getAllUser();
    }

    @PutMapping("/customer/{customerId}")
    public ResponseEntity   <UpdateResponse> updateCustomer(@Valid @RequestBody UpdateProfile updateProfile) {
        UpdateResponse update = userService.updateCustomerProfile(updateProfile);
        return ResponseEntity.ok(update);
    }

    @PostMapping("/request-reset-password")
    public ResponseEntity<String> requestReset(@RequestParam("username") String username) {
        Users user = userRepository.findUsersByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        String token = tokenService.generateResetPasswordToken(user);

        EmailDetail detail = new EmailDetail();
        detail.setReceiver(user);
        detail.setSubject("Reset your password");
        detail.setToken(token);
        emailService.sendEmail(detail, "resetPassword");

        return ResponseEntity.ok("Password reset email sent.");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> showResetPasswordForm(@RequestParam("token") String token) {
        boolean valid = tokenService.validateResetToken(token);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired token.");
        }
        return ResponseEntity.ok("Token is valid. You can now reset your password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {

        boolean valid = tokenService.validateResetToken(token);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired token.");
        }

        boolean updated = userService.updatePasswordByToken(token, newPassword);
        if (updated) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to reset password.");
        }
    }
}
