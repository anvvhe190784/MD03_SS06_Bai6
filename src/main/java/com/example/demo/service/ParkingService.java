package com.example.demo.service;

import com.example.demo.dto.TicketRequest;
import com.example.demo.dto.TicketResponse;
import com.example.demo.entity.ParkingTicket;
import com.example.demo.entity.Vehicle;
import com.example.demo.entity.Zone;
import com.example.demo.repository.ParkingTicketRepository;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ParkingService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private ParkingTicketRepository parkingTicketRepository;

    @Transactional
    public TicketResponse checkIn(TicketRequest req) {
        if (req.getVehicleId() == null) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        if (req.getZoneId() == null) {
            throw new IllegalArgumentException("Zone ID is required");
        }

        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + req.getVehicleId()));

        Zone zone = zoneRepository.findById(req.getZoneId())
                .orElseThrow(() -> new IllegalArgumentException("Zone not found with ID: " + req.getZoneId()));

        // Check if there is already an active ticket for this vehicle
        parkingTicketRepository.findFirstByVehicleIdAndCheckOutTimeIsNullOrderByIdDesc(vehicle.getId())
                .ifPresent(ticket -> {
                    throw new IllegalStateException("Vehicle " + vehicle.getLicensePlate() + " is already in the parking lot");
                });

        if (zone.getOccupiedSpots() >= zone.getCapacity()) {
            throw new IllegalStateException("Zone " + zone.getName() + " is full");
        }

        // Create new ticket
        ParkingTicket ticket = ParkingTicket.builder()
                .vehicle(vehicle)
                .zone(zone)
                .checkInTime(LocalDateTime.now())
                .build();

        ticket = parkingTicketRepository.save(ticket);

        // Update zone capacity
        zone.setOccupiedSpots(zone.getOccupiedSpots() + 1);
        zoneRepository.save(zone);

        return TicketResponse.builder()
                .id(ticket.getId())
                .licensePlate(vehicle.getLicensePlate())
                .zoneName(zone.getName())
                .checkInTime(ticket.getCheckInTime())
                .checkOutTime(ticket.getCheckOutTime())
                .build();
    }

    @Transactional
    public TicketResponse checkOut(Long vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        ParkingTicket ticket = parkingTicketRepository.findFirstByVehicleIdAndCheckOutTimeIsNullOrderByIdDesc(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("No active parking ticket found for vehicle with ID: " + vehicleId));

        Zone zone = ticket.getZone();

        // Update ticket
        ticket.setCheckOutTime(LocalDateTime.now());
        ticket = parkingTicketRepository.save(ticket);

        // Update zone capacity
        if (zone.getOccupiedSpots() > 0) {
            zone.setOccupiedSpots(zone.getOccupiedSpots() - 1);
            zoneRepository.save(zone);
        }

        return TicketResponse.builder()
                .id(ticket.getId())
                .licensePlate(vehicle.getLicensePlate())
                .zoneName(zone.getName())
                .checkInTime(ticket.getCheckInTime())
                .checkOutTime(ticket.getCheckOutTime())
                .build();
    }
}
