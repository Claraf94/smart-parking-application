package com.smartparking.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.smartparking.entity.ResetPassword;
import com.smartparking.entity.Users;
import com.smartparking.enums.UserType;
import com.smartparking.exceptions.PlatformExceptions.ExistentEmailException;
import com.smartparking.repository.UsersRepository;

@Service //this class is a user service component
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ResetPasswordService resetPasswordService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenEmailService tokenEmailService;

    //register a new user
    public Users registerNewUser(Users user) {
        //checking if the user already exists by using the email before register it
        if(usersRepository.existsByEmail(user.getEmail())) {
            throw new ExistentEmailException("This email " + user.getEmail() + " is already registered.");
        }
    //saving user to the database
    return usersRepository.save(user); 
    }

    // Optional is used to handle cases where the user might not exist
    public Optional<Users> findByEmail(String email) {
        //find a user by email
        return usersRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        //check if the email exists in the database
        return usersRepository.existsByEmail(email);
    }

    public List<Users> findByUserType(UserType userT) {
        //find a user by its ype
        return usersRepository.findByUserType(userT);
    }

    public Optional<Users> findById(int userID) {
        //find a user by its ID
        return usersRepository.findById(userID);
    }

    public Users save(Users user){
        //update and save user info
        return usersRepository.save(user);
    }

    public List<Users> findAll() {
        //find all users
        return usersRepository.findAll();
    }

    public boolean resetPasswordWithToken(String tokenPassword, String newPassword){
        Optional<ResetPassword> reset = resetPasswordService.validateToken(tokenPassword);
        if(reset.isEmpty()){
            return false;
        }
        ResetPassword resetP = reset.get();
        Users user = resetP.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);
        resetPasswordService.deleteToken(resetP);
        return true;
    }

    public void sendResetEmail(String email){
        Optional<Users> userP = usersRepository.findByEmail(email);
        if(userP.isEmpty()){
            throw new IllegalArgumentException("No user was found.");
        }
        Users user = userP.get();
        String tokenPassword = UUID.randomUUID().toString();
        resetPasswordService.createResetToken(user, tokenPassword);

        String link = "https://gray-smoke-0c9a20a03.2.azurestaticapps.net/reset-password.html?token=" + tokenPassword;

        String subject = "Reset password";
        String body = "Hello, " + user.getFirstName() + ",\n" 
                    + "To reset your password, please use the link bellow. This link is valid for 15 minutes.\n"
                    + link;  
        tokenEmailService.sendEmail(user.getEmail(), subject, body);
    }

    public boolean updatePassword(String email, String currentPassword, String newPassword){
        Optional<Users> userP = findByEmail(email);
        if(userP.isEmpty()){
            return false;
        }
        Users user = userP.get();
        if(passwordEncoder.matches(currentPassword, user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));
            save(user);
            return true;
        }
        return false;
    }
}//users service class