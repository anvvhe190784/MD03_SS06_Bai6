package com.example.demo.repository;

import com.example.demo.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.ZoneStatisticsResponse;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    @Query("SELECT new com.example.demo.dto.ZoneStatisticsResponse(z.id, z.name, z.capacity, z.occupiedSpots, z.capacity - z.occupiedSpots) " +
           "FROM Zone z")
    List<ZoneStatisticsResponse> findZoneStatistics();
}
