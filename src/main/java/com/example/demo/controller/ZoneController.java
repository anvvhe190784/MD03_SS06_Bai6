package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ZoneStatisticsResponse;
import com.example.demo.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @GetMapping("/api/v1/zones/stats")
    public ApiResponse<List<ZoneStatisticsResponse>> getZoneStatsV1() {
        long startTime = System.nanoTime();
        List<ZoneStatisticsResponse> response = zoneService.getZoneStatsV1();
        long endTime = System.nanoTime();
        
        System.out.println("API v1 (Loop in Service) runtime: " + (endTime - startTime) + " ns");
        
        return ApiResponse.<List<ZoneStatisticsResponse>>builder()
                .success(true)
                .message("Get zone statistics (V1) successfully")
                .data(response)
                .build();
    }

    @GetMapping("/api/v2/zones/stats")
    public ApiResponse<List<ZoneStatisticsResponse>> getZoneStatsV2() {
        long startTime = System.nanoTime();
        List<ZoneStatisticsResponse> response = zoneService.getZoneStatsV2();
        long endTime = System.nanoTime();
        
        System.out.println("API v2 (JPQL Projection) runtime: " + (endTime - startTime) + " ns");
        
        return ApiResponse.<List<ZoneStatisticsResponse>>builder()
                .success(true)
                .message("Get zone statistics (V2) successfully")
                .data(response)
                .build();
    }
}
