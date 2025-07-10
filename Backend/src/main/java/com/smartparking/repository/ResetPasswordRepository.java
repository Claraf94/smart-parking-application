package com.smartparking.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartparking.entity.ResetPassword;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Integer>{
    //find token by the string
    Optional<ResetPassword> findByToken(String token);
}
