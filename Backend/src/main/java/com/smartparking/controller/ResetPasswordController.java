package com.smartparking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.dto.PasswordResetRequest;
import com.smartparking.service.UsersService;

@RestController
@RequestMapping("resetPassword")
//this controller will handle reset password requests
public class ResetPasswordController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/request")
    public ResponseEntity<String> requestReset(@RequestBody PasswordResetRequest request){
        String email = request.getEmail();
        if(email != null && !email.isEmpty()){
            try{
                usersService.sendResetEmail(email);
                return ResponseEntity.ok("Email sent to user.");
            }catch(IllegalArgumentException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.badRequest().body("Email not valid.");
    }
}//reset password controller class
