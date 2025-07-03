package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.EmailDetail;
import com.motherandbabymilk.dto.request.UpdateRequest;
import com.motherandbabymilk.dto.response.UpdateResponse;
import com.motherandbabymilk.dto.request.Login;
import com.motherandbabymilk.dto.request.Registration;
import com.motherandbabymilk.dto.response.RegistrationResponse;
import com.motherandbabymilk.dto.response.UserResponse;
import com.motherandbabymilk.entity.PasswordResetToken;
import com.motherandbabymilk.entity.Roles;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.exception.DuplicateUserException;
import com.motherandbabymilk.repository.TokenRepository;
import com.motherandbabymilk.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    TokenService tokenService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenRepository tokenRepository;

    public UserService() {
    }

    public RegistrationResponse register(Registration register) {
        Users existedUser = this.userRepository.findUsersByUsername(register.getUsername());
        if (existedUser != null) {
            throw new DuplicateUserException("User with this username already exists");
        }
        try {
            Users user = this.modelMapper.map(register, Users.class);

            user.setPassword(this.passwordEncoder.encode(register.getPassword()));
            user.setRegistration_date(new Date(System.currentTimeMillis()));

            if (register.getRole() != null && !register.getRole().isEmpty()) {
                try {
                    Roles role = Roles.valueOf(register.getRole().toUpperCase().replace(" ", "_"));
                    user.setRoles(role);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid role: " + register.getRole());
                }
            } else {
                user.setRoles(Roles.CUSTOMER);
            }

            Users newUser = this.userRepository.save(user);

            // Gửi email chào mừng
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setReceiver(newUser);
            emailDetail.setSubject("Welcome to MnBMilk");
            emailDetail.setLink("https://9012-118-69-70-166.ngrok-free.app");
            this.emailService.sendEmail(emailDetail, "registration");

            String token = this.tokenService.generateToken(newUser);
            RegistrationResponse response = this.modelMapper.map(newUser, RegistrationResponse.class);
            response.setToken(token);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage());
        }
    }

    public UserResponse login(Login loginDto) {
        try {
            Authentication authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );
            Users user = (Users) authentication.getPrincipal();
            UserResponse userResponse = this.modelMapper.map(user, UserResponse.class);
            userResponse.setToken(this.tokenService.generateToken(user));
            return userResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("Invalid email or password!!");
        }
    }

    public void logout() {}

    public UpdateResponse updateCustomerProfile(UpdateRequest updateProfile) {
        Users currentUser = this.getCurrentAccount();
        if (currentUser == null) {
            throw new EntityNotFoundException("Customer not found!");
        } else {
            currentUser.setFullName(updateProfile.getFullName());
            currentUser.setPhone(updateProfile.getPhone());
            currentUser.setAddress(updateProfile.getAddress());
            Users updatedUser = this.userRepository.save(currentUser);
            return this.modelMapper.map(updatedUser, UpdateResponse.class);
        }
    }

    public Users delete(int id) {
        Users user = this.getUserById(id);
        user.setStatus(false);
        return this.userRepository.save(user);
    }

    public Users getUserById(int id) {
        Users user = this.userRepository.findUsersById(id);
        if (user == null) {
            throw new EntityNotFoundException("User not found!");
        } else {
            return user;
        }
    }

    public List getAllUser() {
        List<Users> userResponse = this.userRepository.findAll();
        return userResponse.stream().map((user) -> {
            UserResponse users = new UserResponse();
            users.setId(user.getId());
            users.setUsername(user.getUsername());
            users.setRoles(user.getRoles());
            users.setFullName(user.getFullName());
            users.setPhone(user.getPhone());
            users.setAddress(user.getAddress());
            users.setStatus(user.isStatus());
            return users;
        }).collect(Collectors.toList());
    }

    public UpdateResponse updateUserByAdmin(int id, UpdateRequest updateProfile) {
        Users user = this.userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found!"));

        user.setFullName(updateProfile.getFullName());
        user.setRoles(updateProfile.getRoles());
        user.setPhone(updateProfile.getPhone());
        user.setAddress(updateProfile.getAddress());
        user.setStatus(updateProfile.isStatus());
        Users updatedUser = this.userRepository.save(user);
        return this.modelMapper.map(updatedUser, UpdateResponse.class);
    }



    public Users getCurrentAccount() {
        return (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean updatePasswordByToken(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            return false;
        }

        Users user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = this.userRepository.findUsersByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public UpdateResponse getCurrentUserInfo() {
        Users currentUser = this.getCurrentAccount();
        if (currentUser == null) {
            throw new EntityNotFoundException("User not found!");
        } else {
            return this.modelMapper.map(currentUser, UpdateResponse.class);
        }
    }
}