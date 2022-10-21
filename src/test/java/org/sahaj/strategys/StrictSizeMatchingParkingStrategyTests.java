package org.sahaj.strategys;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sahaj.ParkingSpot;
import org.sahaj.Size;
import org.sahaj.Vehicle;
import org.sahaj.VehicleType;
import org.sahaj.VehicleType.Bike;
import org.sahaj.VehicleType.Car;
import org.sahaj.VehicleType.Truck;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.sahaj.Size.LARGE;
import static org.sahaj.Size.MEDIUM;
import static org.sahaj.Size.SMALL;

class StrictSizeMatchingParkingStrategyTests {

    @ParameterizedTest
    @MethodSource(value = "unsupportedParameters")
    void returnEmptyParkingSpotForUnsupportedVehicles(Vehicle vehicle, Map<VehicleType, Size> vehicleTypeSizeMap) {
        final var spotsWithEverything = Set.of(new ParkingSpot("A1", MEDIUM),
            new ParkingSpot("A2", LARGE),
            new ParkingSpot("A3", Size.SMALL));
        final var strictSizeMatchingParkingStrategy = new StrictSizeMatchingParkingStrategy(vehicleTypeSizeMap);

        var mayBeParkingSpot = strictSizeMatchingParkingStrategy.findOneFor(vehicle, spotsWithEverything);

        assertTrue(mayBeParkingSpot.isEmpty());
    }

    @ParameterizedTest
    @MethodSource(value = "nonAvailableParameters")
    void returnEmptyWhenMatchingSpotDoesNotExists(Vehicle vehicle, Set<ParkingSpot> spots) {
        final var vehicleSizeMap = Map.<VehicleType, Size>of(new Car(), MEDIUM,
            new Bike(), SMALL,
            new Truck(), LARGE);
        final var strictSizeMatchingParkingStrategy = new StrictSizeMatchingParkingStrategy(vehicleSizeMap);

        var mayBeParkingSpot = strictSizeMatchingParkingStrategy.findOneFor(vehicle, spots);

        assertTrue(mayBeParkingSpot.isEmpty());
    }

    @ParameterizedTest
    @MethodSource(value = "availableParameters")
    void returnAvailableParkingSpot(Vehicle vehicle, Set<ParkingSpot> spots) {
        final var vehicleSizeMap = Map.<VehicleType, Size>of(new Car(), MEDIUM,
            new Bike(), SMALL,
            new Truck(), LARGE);
        final var strictSizeMatchingParkingStrategy = new StrictSizeMatchingParkingStrategy(vehicleSizeMap);

        var mayBeParkingSpot = strictSizeMatchingParkingStrategy.findOneFor(vehicle, spots);

        assertTrue(mayBeParkingSpot.isPresent());
    }


    static Stream<Arguments> unsupportedParameters() {
        return Stream.of(of(new Vehicle(new Car()), Map.of(new Bike(), SMALL, new Truck(), LARGE)),
            of(new Vehicle(new Bike()), Map.of(new Car(), MEDIUM, new Truck(), LARGE)),
            of(new Vehicle(new Truck()), Map.of(new Bike(), MEDIUM, new Car(), LARGE)));
    }

    static Stream<Arguments> nonAvailableParameters() {
        return Stream.of(
            of(new Vehicle(new Car()), Set.of(new ParkingSpot("A1", LARGE), new ParkingSpot("A2", SMALL))),
            of(new Vehicle(new Bike()), Set.of(new ParkingSpot("A1", LARGE), new ParkingSpot("A2", MEDIUM))),
            of(new Vehicle(new Truck()), Set.of(new ParkingSpot("A1", MEDIUM), new ParkingSpot("A2", SMALL))));
    }

    static Stream<Arguments> availableParameters() {
        return Stream.of(of(new Vehicle(new Car()), Set.of(new ParkingSpot("A1", MEDIUM))),
            of(new Vehicle(new Bike()), Set.of(new ParkingSpot("A1", SMALL))),
            of(new Vehicle(new Truck()), Set.of(new ParkingSpot("A1", LARGE))));
    }
}
