package com.smartparking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.dto.EmailRequest;
import com.smartparking.service.UsersService;

@RestController
@RequestMapping("resetPassword")
//this controller will handle reset password requests
public class ResetPasswordController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/request")
    public ResponseEntity<String> requestReset(@RequestBody EmailRequest request){
        String email = request.getEmail();
        if(email != null && !email.isEmpty()){
            try{
                usersService.sendResetEmail(email);
                return ResponseEntity.ok("Email sent to user.");
            }catch(IllegalArgumentException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }catch(Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while trying to request the token: " + e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Email not valid.");
    }
}//reset password controller class
