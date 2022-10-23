package org.sahaj.strategys;

import org.sahaj.common.ParkingResult;
import org.sahaj.common.ParkingSpot;
import org.sahaj.common.Vehicle;

import java.util.Set;

public interface ParkingSpotAllocationStrategy {

    ParkingResult<ParkingSpot> findOneFor(Vehicle vehicle, Set<ParkingSpot> spots);
}
