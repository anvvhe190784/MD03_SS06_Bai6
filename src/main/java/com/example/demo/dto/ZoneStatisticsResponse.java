package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneStatisticsResponse {
    private Long id;
    private String name;
    private Integer capacity;
    private Integer occupiedSlots;
    private Integer availableSlots;
}
