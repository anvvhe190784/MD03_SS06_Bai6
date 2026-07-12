package com.example.demo.service;

import com.example.demo.dto.ZoneStatisticsResponse;
import com.example.demo.entity.Zone;
import com.example.demo.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;

    @Transactional(readOnly = true)
    public List<ZoneStatisticsResponse> getZoneStatsV1() {
        List<Zone> zones = zoneRepository.findAll();
        return zones.stream()
                .map(zone -> ZoneStatisticsResponse.builder()
                        .id(zone.getId())
                        .name(zone.getName())
                        .capacity(zone.getCapacity())
                        .occupiedSlots(zone.getOccupiedSpots())
                        .availableSlots(zone.getCapacity() - zone.getOccupiedSpots())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ZoneStatisticsResponse> getZoneStatsV2() {
        return zoneRepository.findZoneStatistics();
    }
}
