package com.example.scheduling_meetings.repository;

import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, Long> {
    @Query("SELECT a FROM AvailabilityEntity a WHERE a.user.id = :userId")
    List<AvailabilityEntity> findByUserId(@Param("userId") Long userId);
}