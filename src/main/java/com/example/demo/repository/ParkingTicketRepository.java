package com.example.demo.repository;

import com.example.demo.entity.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.TicketResponse;
import com.example.demo.dto.TicketSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {
    Optional<ParkingTicket> findFirstByVehicleIdAndCheckOutTimeIsNullOrderByIdDesc(Long vehicleId);

    @Query("SELECT new com.example.demo.dto.TicketSummaryResponse(t.id, v.licensePlate, z.name, t.checkInTime, t.checkOutTime) " +
           "FROM ParkingTicket t " +
           "JOIN t.vehicle v " +
           "JOIN t.zone z " +
           "WHERE t.checkInTime >= :startOfDay AND t.checkInTime < :endOfDay")
    List<TicketSummaryResponse> findTicketsSummaryOfDay(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT new com.example.demo.dto.TicketResponse(t.id, v.licensePlate, z.name, t.checkInTime, t.checkOutTime) " +
           "FROM ParkingTicket t " +
           "JOIN t.vehicle v " +
           "JOIN t.zone z " +
           "WHERE (:licensePlate IS NULL OR v.licensePlate = :licensePlate) " +
           "AND (t.checkInTime >= :fromDate) " +
           "AND (t.checkInTime <= :toDate)")
    Page<TicketResponse> findHistory(
            @Param("licensePlate") String licensePlate,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}
