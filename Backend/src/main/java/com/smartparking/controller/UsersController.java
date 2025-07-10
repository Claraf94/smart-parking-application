package com.smartparking.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.dto.PasswordResetRequest;
import com.smartparking.dto.PasswordUpdateRequest;
import com.smartparking.entity.Users;
import com.smartparking.security.JWTAuthentication;
import com.smartparking.service.UsersService;

@RestController
@RequestMapping("/users")
//this controller handles all operations related to users, including registration, retrieval by email, and checking if an email exists.
public class UsersController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTAuthentication jwtAuthentication;

    //register a new user
    @PostMapping("/register")
    public Users registerNewUser(@RequestBody Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); //encode the password before saving
        return usersService.registerNewUser(user);
    }

    //returns a user by its email
    @GetMapping("/findByEmail/{email}")
    public Optional<Users> findByEmail(@PathVariable String email) {
        return usersService.findByEmail(email);
    }

    //checks if an email has already been registered
    @GetMapping("/emailExists/{email}")
    public boolean emailExists(@PathVariable String email) {
        return usersService.emailExists(email);
    }
    
    //returns users by its type
    @GetMapping("/findByUserType/{userType}")
    public List<Users> findByUserType(@PathVariable String userType) {
        return usersService.findByUserType(userType);
    }

    //returns all users (admin tool)
    @PreAuthorize("hasRole('ADMIN')") //only admin can access this endpoint
    @GetMapping("/findAll")
    public List<Users> findAll() {
        return usersService.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Users loginRequest) {
        //logic to authenticate user and generate JWT token
        Optional<Users> existentUser = usersService.findByEmail(loginRequest.getEmail());
        if (existentUser.isPresent()){
            Users user = existentUser.get();
            if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
                List<String> roles = List.of("ROLE_USER");
                //separating message and token for clarity when tracking login attempts
                return ResponseEntity.ok(Map.of("message", "Login successful, token generated " + jwtAuthentication.generateSecurityToken(user.getEmail(), roles)));
            }
        }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Error", "Invalid email or password"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    //request to set a new password without being loggedin
    @PostMapping("/resetTokenPassword")
    public ResponseEntity<String> requestResetPassword(@RequestBody PasswordResetRequest request){
        boolean resetOperation = usersService.resetPasswordWithToken(request.getTokenPassword(), request.getNewPassword());
        if(resetOperation){
            return ResponseEntity.ok("Password was reset successfully.");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token for the operation.");
        }
    }

    //request to set a new password being loggedin
    @PostMapping("/updatePassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordUpdateRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
        String email = authentication.getName();
        if(email == null || email.isBlank()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email.");
        }
        
        if(usersService.findByEmail(email).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        Users user = usersService.findByEmail(email).get();
        if(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            usersService.save(user);
            return ResponseEntity.ok("Password was updated successfully.");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not match the current one.");
        }   
    }
}//users controller class