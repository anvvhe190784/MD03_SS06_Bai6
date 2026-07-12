package com.example.demo.dto;

import com.example.demo.entity.VehicleType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleCreateRequest {
    private String licensePlate;
    private String color;
    private VehicleType type;
}
