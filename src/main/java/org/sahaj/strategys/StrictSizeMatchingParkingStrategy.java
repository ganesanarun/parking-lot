package org.sahaj.strategys;

import org.sahaj.ParkingSpot;
import org.sahaj.Size;
import org.sahaj.Vehicle;
import org.sahaj.VehicleType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class StrictSizeMatchingParkingStrategy {

    private final Map<VehicleType, Size> vehicleTypeSizeMap;

    // TODO, introduce locking service to make it distributable

    public StrictSizeMatchingParkingStrategy(Map<VehicleType, Size> vehicleTypeSizeMap) {
        this.vehicleTypeSizeMap = vehicleTypeSizeMap;
    }

    public Optional<ParkingSpot> findOneFor(Vehicle vehicle, List<ParkingSpot> spots) {
        if (!vehicleTypeSizeMap.containsKey(vehicle.vehicleType())) {
            return Optional.empty();
        }
        final var size = vehicleTypeSizeMap.get(vehicle.vehicleType());
        return parkingSpotsOf(size, spots).findFirst();
    }

    static Stream<ParkingSpot> parkingSpotsOf(Size size, List<ParkingSpot> spots) {
        return spots.stream().filter(parkingSpot -> parkingSpot.size().equals(size));
    }
}