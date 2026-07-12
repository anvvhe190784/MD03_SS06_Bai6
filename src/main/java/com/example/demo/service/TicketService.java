package com.example.demo.service;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.TicketResponse;
import com.example.demo.dto.TicketSummaryResponse;
import com.example.demo.repository.ParkingTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private ParkingTicketRepository parkingTicketRepository;

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getTodayTicketsSummary() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        return parkingTicketRepository.findTicketsSummaryOfDay(startOfDay, endOfDay);
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketResponse> getHistory(
            String licensePlate,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size
    ) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        // Sắp xếp theo thời gian mới nhất (checkInTime DESC)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "checkInTime"));

        String searchPlate = (licensePlate == null || licensePlate.trim().isEmpty()) ? null : licensePlate.trim();
        LocalDateTime start = (fromDate == null) ? LocalDateTime.of(1970, 1, 1, 0, 0) : fromDate;
        LocalDateTime end = (toDate == null) ? LocalDateTime.of(2099, 12, 31, 23, 59) : toDate;

        Page<TicketResponse> ticketPage = parkingTicketRepository.findHistory(searchPlate, start, end, pageable);

        return PageResponse.<TicketResponse>builder()
                .items(ticketPage.getContent())
                .page(ticketPage.getNumber())
                .size(ticketPage.getSize())
                .totalItems(ticketPage.getTotalElements())
                .totalPages(ticketPage.getTotalPages())
                .isLast(ticketPage.isLast())
                .build();
    }
}
