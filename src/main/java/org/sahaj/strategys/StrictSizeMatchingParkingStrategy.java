package org.sahaj.strategys;

import org.sahaj.common.ParkingResult;
import org.sahaj.common.ParkingResult.NoOpenParkingSpot;
import org.sahaj.common.ParkingResult.Success;
import org.sahaj.common.ParkingResult.UnsupportedVehicle;
import org.sahaj.common.ParkingSpot;
import org.sahaj.common.Size;
import org.sahaj.common.Vehicle;
import org.sahaj.common.VehicleType;
import org.sahaj.common.VehicleType.Bike;
import org.sahaj.common.VehicleType.Car;
import org.sahaj.common.VehicleType.Truck;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class StrictSizeMatchingParkingStrategy implements ParkingSpotAllocationStrategy {

    private final Map<VehicleType, Size> vehicleTypeSizeMap;

    // TODO, introduce locking service to make it distributable

    public StrictSizeMatchingParkingStrategy(Map<VehicleType, Size> vehicleTypeSizeMap) {
        this.vehicleTypeSizeMap = vehicleTypeSizeMap;
    }

    @Override
    public ParkingResult<ParkingSpot> findOneFor(Vehicle vehicle, Set<ParkingSpot> spots) {
        // Doing this way to compiler to throw an error if new Vehicle type is introduced
        return switch (vehicle.vehicleType()) {
            case Car c && !vehicleTypeSizeMap.containsKey(c) -> new UnsupportedVehicle<>();
            case Car c -> getParkingSpotSuccess(spots, c);
            case Bike b && !vehicleTypeSizeMap.containsKey(b) -> new UnsupportedVehicle<>();
            case Bike b -> getParkingSpotSuccess(spots, b);
            case Truck t && !vehicleTypeSizeMap.containsKey(t) -> new UnsupportedVehicle<>();
            case Truck t -> getParkingSpotSuccess(spots, t);
        };
    }

    private ParkingResult<ParkingSpot> getParkingSpotSuccess(Set<ParkingSpot> spots, VehicleType vehicleType) {
        return parkingSpotsOf(vehicleTypeSizeMap.get(vehicleType), spots)
            .findFirst()
            .map(parkingSpot -> (ParkingResult<ParkingSpot>) new Success<>(parkingSpot))
            .orElse(new NoOpenParkingSpot<>());
    }

    static Stream<ParkingSpot> parkingSpotsOf(Size size, Set<ParkingSpot> spots) {
        return spots.stream().filter(parkingSpot -> parkingSpot.size().equals(size));
    }
}
