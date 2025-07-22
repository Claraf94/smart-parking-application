package com.smartparking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smartparking.dto.EmailRequest;
import com.smartparking.dto.PasswordResetRequest;
import com.smartparking.service.UsersService;
import java.util.Map;

@RestController
@RequestMapping("/resetPassword")
// this controller will handle reset password requests
public class ResetPasswordController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/request")
    public ResponseEntity<String> requestReset(@RequestBody EmailRequest request) {
        String email = request.getEmail();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email provided is not valid.");
        }
        try {
            usersService.sendResetEmail(email);
            return ResponseEntity.ok("Email sent to user.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while trying to request the token: " + e.getMessage());
        }
    }

    // request to set a new password without being logged in
    @PutMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestParam String token, @RequestBody PasswordResetRequest request) {
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmNewPassword();

        if (newPassword == null || confirmPassword == null || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Both password fields must be filled."));
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Passwords do not match."));
        }

        try {
            boolean reset = usersService.resetPasswordWithToken(token, newPassword);
            if (reset) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Password successfully reset."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Invalid or expired token for the operation."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error while resetting password: " + e.getMessage()));
        }
    }

}// reset password controller class
