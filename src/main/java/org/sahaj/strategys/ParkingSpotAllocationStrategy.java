package org.sahaj.strategys;

import org.sahaj.ParkingSpot;
import org.sahaj.Vehicle;

import java.util.Optional;
import java.util.Set;

public interface ParkingSpotAllocationStrategy {

    Optional<ParkingSpot> findOneFor(Vehicle vehicle, Set<ParkingSpot> spots);
}
