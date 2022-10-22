package org.sahaj.parking;

import org.sahaj.common.Allocation;
import org.sahaj.common.ParkingSpot;
import org.sahaj.common.ParkingTicket;
import org.sahaj.common.Vehicle;
import org.sahaj.strategys.ParkingSpotAllocationStrategy;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ParkingFloor {

    private final Set<ParkingSpot> freeParkingSpots;

    private final Map<String, Allocation> allocationMap;

    private final ParkingSpotAllocationStrategy parkingSpotAllocationStrategy;


    public ParkingFloor(Set<ParkingSpot> freeParkingSpots,
        Set<Allocation> allocations,
        ParkingSpotAllocationStrategy parkingSpotAllocationStrategy) {
        this.freeParkingSpots = new HashSet<>(freeParkingSpots);
        this.allocationMap = mapFrom(allocations);
        this.parkingSpotAllocationStrategy = parkingSpotAllocationStrategy;
    }

    public ParkingFloor(Set<ParkingSpot> freeParkingSpots,
        ParkingSpotAllocationStrategy parkingSpotAllocationStrategy) {
        this.freeParkingSpots = new HashSet<>(freeParkingSpots);
        this.allocationMap = new HashMap<>();
        this.parkingSpotAllocationStrategy = parkingSpotAllocationStrategy;
    }

    Optional<Allocation> parkThis(Vehicle vehicle) {
        final var maybeParkingSpot = parkingSpotAllocationStrategy.findOneFor(vehicle, freeParkingSpots);
        if (maybeParkingSpot.isEmpty()) {
            return Optional.empty();
        }
        var parkingSpot = maybeParkingSpot.get();
        freeParkingSpots.remove(parkingSpot);
        final var allocation = new Allocation(UUID.randomUUID().toString(),
            parkingSpot,
            ZonedDateTime.now(),
            vehicle);
        allocationMap.put(parkingSpot.number(), allocation);
        return Optional.of(allocation);
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

