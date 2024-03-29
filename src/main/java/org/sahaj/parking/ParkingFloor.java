package org.sahaj.parking;

import org.sahaj.common.Allocation;
import org.sahaj.common.ParkingResult;
import org.sahaj.common.ParkingResult.NoOpenParkingSpot;
import org.sahaj.common.ParkingResult.Success;
import org.sahaj.common.ParkingResult.UnsupportedVehicle;
import org.sahaj.common.ParkingSpot;
import org.sahaj.common.ParkingTicket;
import org.sahaj.common.Vehicle;
import org.sahaj.strategys.ParkingSpotAllocationStrategy;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ParkingFloor {

    private final Set<ParkingSpot> freeParkingSpots;

    private final Map<String, Allocation> allocationMap;

    private final ParkingSpotAllocationStrategy parkingSpotAllocationStrategy;
    private final TicketNumberGenerator ticketNumberGenerator;


    public ParkingFloor(Set<ParkingSpot> freeParkingSpots,
        Set<Allocation> allocations,
        ParkingSpotAllocationStrategy parkingSpotAllocationStrategy,
        TicketNumberGenerator ticketNumberGenerator) {
        this.freeParkingSpots = new HashSet<>(freeParkingSpots);
        this.allocationMap = mapFrom(allocations);
        this.parkingSpotAllocationStrategy = parkingSpotAllocationStrategy;
        this.ticketNumberGenerator = ticketNumberGenerator;
    }

    public ParkingFloor(Set<ParkingSpot> freeParkingSpots,
        ParkingSpotAllocationStrategy parkingSpotAllocationStrategy,
        TicketNumberGenerator ticketNumberGenerator) {
        this.freeParkingSpots = new HashSet<>(freeParkingSpots);
        this.ticketNumberGenerator = ticketNumberGenerator;
        this.allocationMap = new HashMap<>();
        this.parkingSpotAllocationStrategy = parkingSpotAllocationStrategy;
    }

    ParkingResult<Allocation> parkThis(Vehicle vehicle) {
        final var maybeParkingSpot = parkingSpotAllocationStrategy.findOneFor(vehicle, freeParkingSpots);
        return switch (maybeParkingSpot) {
            case UnsupportedVehicle<?> ignored -> new UnsupportedVehicle<>();
            case NoOpenParkingSpot<?> ignored -> new NoOpenParkingSpot<>();
            case Success<ParkingSpot> success -> {
                var parkingSpot = success.value();
                freeParkingSpots.remove(parkingSpot);
                final var allocation = new Allocation(ticketNumberGenerator.nextOne(),
                    parkingSpot,
                    ZonedDateTime.now(),
                    vehicle);
                allocationMap.put(parkingSpot.number(), allocation);
                yield new Success<>(allocation);
            }
        };
    }

    boolean unParkWith(ParkingTicket ticket) {
        if (!allocationMap.containsKey(ticket.ticketNumber())) {
            return false;
        }
        final var allocation = allocationMap.remove(ticket.ticketNumber());
        freeParkingSpots.add(allocation.parkingSpot());
        return true;
    }

    static Map<String, Allocation> mapFrom(Set<Allocation> allocations) {
        return allocations
            .stream()
            .collect(Collectors.toMap(Allocation::number, allocation -> allocation));
    }
}

