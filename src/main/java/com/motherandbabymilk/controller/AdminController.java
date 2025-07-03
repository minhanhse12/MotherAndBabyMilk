package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.UpdateRequest;
import com.motherandbabymilk.dto.response.UpdateResponse;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(
        origins = {"http://localhost:5173"}
)
@RequestMapping({"/api/admin"})
@SecurityRequirement(
        name = "api"
)
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    UserService userService;

    @PutMapping("/user/{userId}")
    public ResponseEntity<UpdateResponse> updateUserByAdmin(
            @PathVariable ("userId")int id,
            @RequestBody UpdateRequest updateProfile) {

        UpdateResponse update = this.userService.updateUserByAdmin(id, updateProfile);
        return ResponseEntity.ok(update);
    }

    @GetMapping({"/user"})
    public List getAllUsers() {
        return this.userService.getAllUser();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") int userId) {
        Users deletedUsers = userService.delete(userId);
        return ResponseEntity.ok(deletedUsers);
    }
}

