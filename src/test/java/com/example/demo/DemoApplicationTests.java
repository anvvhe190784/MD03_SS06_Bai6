package com.example.demo;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.TicketRequest;
import com.example.demo.dto.TicketResponse;
import com.example.demo.dto.TicketSummaryResponse;
import com.example.demo.dto.VehicleCreateRequest;
import com.example.demo.dto.VehicleResponse;
import com.example.demo.dto.ZoneStatisticsResponse;
import com.example.demo.entity.ParkingTicket;
import com.example.demo.entity.Vehicle;
import com.example.demo.entity.VehicleType;
import com.example.demo.entity.Zone;
import com.example.demo.repository.ParkingTicketRepository;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.repository.ZoneRepository;
import com.example.demo.service.ParkingService;
import com.example.demo.service.TicketService;
import com.example.demo.service.VehicleService;
import com.example.demo.service.ZoneService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private ZoneRepository zoneRepository;

	@Autowired
	private ParkingTicketRepository parkingTicketRepository;

	@Autowired
	private ParkingService parkingService;

	@Autowired
	private TicketService ticketService;

	@Test
	void contextLoads() throws Exception {
		// 1. Verify schema configuration
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			System.out.println("--- VERIFYING TABLES ---");
			try (ResultSet tables = metaData.getTables(null, "public", "vehicles", new String[]{"TABLE"})) {
				if (tables.next()) {
					System.out.println("Table vehicles exists!");
				} else {
					fail("Table vehicles does not exist!");
				}
			}
		}

		// 2. Clear old data for test consistency
		parkingTicketRepository.deleteAll();
		vehicleRepository.deleteAll();
		zoneRepository.deleteAll();

		// 3. Create test data
		VehicleResponse v1 = vehicleService.createVehicle(new VehicleCreateRequest("18A-11111", "Red", VehicleType.CAR));
		VehicleResponse v2 = vehicleService.createVehicle(new VehicleCreateRequest("29B-22222", "Blue", VehicleType.CAR));

		Zone zoneA = Zone.builder().name("Khu A").capacity(10).occupiedSpots(0).build();
		zoneA = zoneRepository.save(zoneA);

		Vehicle vehicle1 = vehicleRepository.findById(v1.getId()).orElseThrow();
		Vehicle vehicle2 = vehicleRepository.findById(v2.getId()).orElseThrow();

		// Insert history manually for exact check-in time control
		// Ticket 1: 18A-11111 at 08:00
		ParkingTicket t1 = ParkingTicket.builder()
				.vehicle(vehicle1)
				.zone(zoneA)
				.checkInTime(LocalDateTime.of(2026, 7, 13, 8, 0, 0))
				.build();
		parkingTicketRepository.save(t1);

		// Ticket 2: 18A-11111 at 10:00
		ParkingTicket t2 = ParkingTicket.builder()
				.vehicle(vehicle1)
				.zone(zoneA)
				.checkInTime(LocalDateTime.of(2026, 7, 13, 10, 0, 0))
				.build();
		parkingTicketRepository.save(t2);

		// Ticket 3: 18A-11111 at 12:00
		ParkingTicket t3 = ParkingTicket.builder()
				.vehicle(vehicle1)
				.zone(zoneA)
				.checkInTime(LocalDateTime.of(2026, 7, 13, 12, 0, 0))
				.build();
		parkingTicketRepository.save(t3);

		// Ticket 4: 29B-22222 at 09:00
		ParkingTicket t4 = ParkingTicket.builder()
				.vehicle(vehicle2)
				.zone(zoneA)
				.checkInTime(LocalDateTime.of(2026, 7, 13, 9, 0, 0))
				.build();
		parkingTicketRepository.save(t4);

		System.out.println("--- TESTING TICKET HISTORY PAGINATION & SORTING ---");
		
		// Test 4.1: Fetch history for "18A-11111", page 0, size 2 (Should get newest 12:00 and 10:00 tickets)
		PageResponse<TicketResponse> historyPage1 = ticketService.getHistory("18A-11111", null, null, 0, 2);
		
		System.out.println("History Page 1 total items: " + historyPage1.getTotalItems());
		System.out.println("History Page 1 total pages: " + historyPage1.getTotalPages());
		for (TicketResponse ticket : historyPage1.getItems()) {
			System.out.println("  - Plate: " + ticket.getLicensePlate() + ", CheckIn: " + ticket.getCheckInTime());
		}

		assertEquals(3, historyPage1.getTotalItems());
		assertEquals(2, historyPage1.getTotalPages());
		assertEquals(2, historyPage1.getItems().size());
		// Sort check: newest first (t3 at 12:00, then t2 at 10:00)
		assertEquals(LocalDateTime.of(2026, 7, 13, 12, 0, 0), historyPage1.getItems().get(0).getCheckInTime());
		assertEquals(LocalDateTime.of(2026, 7, 13, 10, 0, 0), historyPage1.getItems().get(1).getCheckInTime());

		// Test 4.2: Fetch history for "18A-11111", filtered by date range [09:00 - 11:00]
		LocalDateTime from = LocalDateTime.of(2026, 7, 13, 9, 0, 0);
		LocalDateTime to = LocalDateTime.of(2026, 7, 13, 11, 0, 0);
		PageResponse<TicketResponse> historyFiltered = ticketService.getHistory("18A-11111", from, to, 0, 10);
		
		System.out.println("Filtered History count: " + historyFiltered.getTotalItems());
		for (TicketResponse ticket : historyFiltered.getItems()) {
			System.out.println("  - Filtered Plate: " + ticket.getLicensePlate() + ", CheckIn: " + ticket.getCheckInTime());
		}

		assertEquals(1, historyFiltered.getTotalItems());
		assertEquals(LocalDateTime.of(2026, 7, 13, 10, 0, 0), historyFiltered.getItems().get(0).getCheckInTime());

		System.out.println("--- ALL TICKET HISTORY TESTS PASSED ---");
	}

}






