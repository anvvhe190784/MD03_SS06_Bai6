package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.TicketRequest;
import com.example.demo.dto.TicketResponse;
import com.example.demo.dto.TicketSummaryResponse;
import com.example.demo.service.ParkingService;
import com.example.demo.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/summary")
    public ApiResponse<List<TicketSummaryResponse>> getTodayTicketsSummary() {
        List<TicketSummaryResponse> response = ticketService.getTodayTicketsSummary();
        return ApiResponse.<List<TicketSummaryResponse>>builder()
                .success(true)
                .message("Get today's tickets summary successfully")
                .data(response)
                .build();
    }

    @GetMapping("/history")
    public ApiResponse<PageResponse<TicketResponse>> getHistory(
            @RequestParam(value = "licensePlate", required = false) String licensePlate,
            @RequestParam(value = "fromDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime toDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageResponse<TicketResponse> response = ticketService.getHistory(licensePlate, fromDate, toDate, page, size);
        return ApiResponse.<PageResponse<TicketResponse>>builder()
                .success(true)
                .message("Get ticket history successfully")
                .data(response)
                .build();
    }

    @PostMapping("/check-in")
    public ApiResponse<TicketResponse> checkIn(@RequestBody TicketRequest request) {
        TicketResponse response = parkingService.checkIn(request);
        return ApiResponse.<TicketResponse>builder()
                .success(true)
                .message("Check-in successful")
                .data(response)
                .build();
    }

    @PutMapping("/check-out/{vehicleId}")
    public ApiResponse<TicketResponse> checkOut(@PathVariable("vehicleId") Long vehicleId) {
        TicketResponse response = parkingService.checkOut(vehicleId);
        return ApiResponse.<TicketResponse>builder()
                .success(true)
                .message("Check-out successful")
                .data(response)
                .build();
    }
}
