package com.smartparking.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartparking.entity.ResetPassword;
import com.smartparking.entity.Users;
import com.smartparking.repository.ResetPasswordRepository;

@Service //this classe is a reset password service component
public class ResetPasswordService {
    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    //creating a token to reset the password for the user
    public ResetPassword createResetToken(Users user, String token){
        ResetPassword reset = new ResetPassword();
        reset.setUser(user);
        reset.setToken(token);
        //token will be expired in 15 minutes after its creation 
        reset.setExpirationTime(LocalDateTime.now().plusMinutes(15));
        return resetPasswordRepository.save(reset);
    }

    //validating the token when used
    public Optional<ResetPassword> validateToken(String token){
        Optional<ResetPassword> resetP = resetPasswordRepository.findByToken(token);
        if(resetP.isPresent()){
            ResetPassword reset = resetP.get();
            if(reset.getExpirationTime().isAfter(LocalDateTime.now())){
                return resetP;
            }else{
                resetPasswordRepository.delete(reset);
            }
        }
        return Optional.empty();
    }

    //delete token after being used
    public void deleteToken(ResetPassword reset){
        resetPasswordRepository.delete(reset);
    }

}
