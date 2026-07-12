package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.VehicleCreateRequest;
import com.example.demo.dto.VehicleResponse;
import com.example.demo.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ApiResponse<VehicleResponse> createVehicle(@RequestBody VehicleCreateRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ApiResponse.<VehicleResponse>builder()
                .success(true)
                .message("Vehicle created successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<VehicleResponse>> getVehicles(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        PageResponse<VehicleResponse> response = vehicleService.getPagedVehicles(page, size, sortBy, direction, keyword);
        return ApiResponse.<PageResponse<VehicleResponse>>builder()
                .success(true)
                .message("Get vehicles successfully")
                .data(response)
                .build();
    }
}
