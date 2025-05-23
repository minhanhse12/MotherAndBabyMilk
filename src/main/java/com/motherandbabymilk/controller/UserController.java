package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.*;
import com.motherandbabymilk.dto.request.Login;
import com.motherandbabymilk.dto.request.Registration;
import com.motherandbabymilk.dto.response.RegistrationResponse;
import com.motherandbabymilk.dto.response.UpdateResponse;
import com.motherandbabymilk.dto.response.UserResponse;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.repository.UserRepository;
import com.motherandbabymilk.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

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

//    @GetMapping("/user")
//    public List<Users> getAllUsers() {
//        return userService.getAllUser();
//    }

    @PutMapping("/customer/{customerId}")
    public ResponseEntity<UpdateResponse> updateCustomer(@Valid @RequestBody UpdateProfile updateProfile) {
        UpdateResponse update = userService.updateCustomerProfile(updateProfile);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") int userId) {
        Users deletedUsers = userService.delete(userId);
        return ResponseEntity.ok(deletedUsers);
    }
}
