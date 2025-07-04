package com.smartparking.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.entity.Users;
import com.smartparking.service.UsersService;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;
    //register a new user
    @PostMapping("/register")
    public Users registerNewUser(@RequestBody Users user) {
        return usersService.registerNewUser(user);
    }

    //request to find a user by its email
    @GetMapping("/findByEmail/{email}")
    public Optional<Users> findByEmail(@PathVariable String email) {
        return usersService.findByEmail(email);
    }

    //request to check if an email exists
    @GetMapping("/emailExists/{email}")
    public boolean emailExists(@PathVariable String email) {
        return usersService.emailExists(email);
    }
    
    //request to find users by their type
    @GetMapping("/findByUserType/{userType}")
    public List<Users> findByUserType(@PathVariable String userType) {
        return usersService.findByUserType(userType);
    }

    //request to find all users
    @PreAuthorize("hasRole('ADMIN')") //only admin can access this endpoint
    @GetMapping("/findAll")
    public List<Users> findAll() {
        return usersService.findAll();
    }
}//users controller class
