package com.smartparking.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.smartparking.entity.Users;
import com.smartparking.enums.UserType;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    //find a user by email. Optional is used to handle cases where the user might not exist
    Optional<Users> findByEmail(String email);
    //verify if a user exists by email
    boolean existsByEmail(String email);
    //find a user by type
    List<Users> findByUserType(UserType userType);
}//users repository class