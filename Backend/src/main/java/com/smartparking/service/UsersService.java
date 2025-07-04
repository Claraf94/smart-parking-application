package com.smartparking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartparking.entity.Users;
import com.smartparking.exceptions.PlatformExceptions.ExistentEmailException;
import com.smartparking.repository.UsersRepository;

@Service //this class is a user service component
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;
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

    public List<Users> findByUserType(String userType) {
        //find a user by its ype
        return usersRepository.findByUserType(userType);
    }

    public List<Users> findAll() {
        //find all users
        return usersRepository.findAll();
    }
}//users service class