package com.smartparking.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartparking.entity.Spots;

@Repository
public interface SpotsRepository extends JpaRepository<Spots, Integer> {
    //find a spot by its Status
    List<Spots> findByStatus(String status);
    //find a spot that can be reserved
    List<Spots> findByIsReservableTrue();
    //find a spot by its status and reservable conditions
    List<Spots> findByStatusAndIsReservable(String status, boolean isReservable);
    //find a spot by its code.  Optional is used to handle cases where the spot might not exist
    Optional<Spots> findBySpotCode(String spotCode);
}//spots repository class