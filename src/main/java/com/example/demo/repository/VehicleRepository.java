package com.example.demo.repository;

import com.example.demo.dto.VehicleResponse;
import com.example.demo.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT new com.example.demo.dto.VehicleResponse(v.id, v.licensePlate, v.color, v.type) " +
           "FROM Vehicle v " +
           "WHERE :keyword IS NULL OR " +
           "LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.color) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<VehicleResponse> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
