package com.example.demo.service;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.VehicleCreateRequest;
import com.example.demo.dto.VehicleResponse;
import com.example.demo.entity.Vehicle;
import com.example.demo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public PageResponse<VehicleResponse> getPagedVehicles(int page, int size, String sortBy, String direction, String keyword) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort.Direction dir = Sort.Direction.ASC;
            if (direction != null && direction.trim().equalsIgnoreCase("DESC")) {
                dir = Sort.Direction.DESC;
            }
            sort = Sort.by(dir, sortBy.trim());
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();

        Page<VehicleResponse> vehiclePage = vehicleRepository.findAllByKeyword(searchKeyword, pageable);

        return PageResponse.<VehicleResponse>builder()
                .items(vehiclePage.getContent())
                .page(vehiclePage.getNumber())
                .size(vehiclePage.getSize())
                .totalItems(vehiclePage.getTotalElements())
                .totalPages(vehiclePage.getTotalPages())
                .isLast(vehiclePage.isLast())
                .build();
    }

    @Transactional
    public VehicleResponse createVehicle(VehicleCreateRequest request) {
        if (request.getLicensePlate() == null || request.getLicensePlate().trim().isEmpty()) {
            throw new IllegalArgumentException("License plate is required");
        }
        if (request.getColor() == null || request.getColor().trim().isEmpty()) {
            throw new IllegalArgumentException("Color is required");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(request.getLicensePlate().trim())
                .color(request.getColor().trim())
                .type(request.getType())
                .build();

        vehicle = vehicleRepository.save(vehicle);

        return VehicleResponse.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .color(vehicle.getColor())
                .vehicleType(vehicle.getType())
                .build();
    }
}
