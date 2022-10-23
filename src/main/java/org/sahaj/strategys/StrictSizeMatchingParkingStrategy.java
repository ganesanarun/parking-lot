package org.sahaj.strategys;

import org.sahaj.common.ParkingSpot;
import org.sahaj.common.Size;
import org.sahaj.common.Vehicle;
import org.sahaj.common.VehicleType;
import org.sahaj.common.VehicleType.Bike;
import org.sahaj.common.VehicleType.Car;
import org.sahaj.common.VehicleType.Truck;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class StrictSizeMatchingParkingStrategy implements ParkingSpotAllocationStrategy {

    private final Map<VehicleType, Size> vehicleTypeSizeMap;

    // TODO, introduce locking service to make it distributable

    public StrictSizeMatchingParkingStrategy(Map<VehicleType, Size> vehicleTypeSizeMap) {
        this.vehicleTypeSizeMap = vehicleTypeSizeMap;
    }

    @Override
    public Optional<ParkingSpot> findOneFor(Vehicle vehicle, Set<ParkingSpot> spots) {
        // Doing this way to compiler to throw an error if new Vehicle type is introduced
        return switch (vehicle.vehicleType()) {
            case Car c && !vehicleTypeSizeMap.containsKey(c) -> Optional.empty();
            case Car c -> parkingSpotsOf(vehicleTypeSizeMap.get(c), spots).findFirst();
            case Bike b && !vehicleTypeSizeMap.containsKey(b) -> Optional.empty();
            case Bike b -> parkingSpotsOf(vehicleTypeSizeMap.get(b), spots).findFirst();
            case Truck t && !vehicleTypeSizeMap.containsKey(t) -> Optional.empty();
            case Truck t -> parkingSpotsOf(vehicleTypeSizeMap.get(t), spots).findFirst();
        };
    }

    static Stream<ParkingSpot> parkingSpotsOf(Size size, Set<ParkingSpot> spots) {
        return spots.stream().filter(parkingSpot -> parkingSpot.size().equals(size));
    }
}
