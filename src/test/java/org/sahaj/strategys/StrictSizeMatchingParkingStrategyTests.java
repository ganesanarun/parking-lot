package org.sahaj.strategys;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sahaj.common.ParkingResult;
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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.sahaj.common.Size.LARGE;
import static org.sahaj.common.Size.MEDIUM;
import static org.sahaj.common.Size.SMALL;

class StrictSizeMatchingParkingStrategyTests {

    @ParameterizedTest
    @MethodSource(value = "unsupportedParameters")
    void returnUnsupportedResultForUnsupportedVehicles(Vehicle vehicle, Map<VehicleType, Size> vehicleTypeSizeMap) {
        final var spotsWithEverything = Set.of(new ParkingSpot("A1", MEDIUM),
            new ParkingSpot("A2", LARGE),
            new ParkingSpot("A3", Size.SMALL));
        final var strictSizeMatchingParkingStrategy = new StrictSizeMatchingParkingStrategy(vehicleTypeSizeMap);

        var mayBeParkingSpot = strictSizeMatchingParkingStrategy.findOneFor(vehicle, spotsWithEverything);

        assertInstanceOf(ParkingResult.UnsupportedVehicle.class, mayBeParkingSpot);
    }

    @ParameterizedTest
    @MethodSource(value = "nonAvailableParameters")
    void returnNoParkingResultWhenMatchingSpotDoesNotExists(Vehicle vehicle, Set<ParkingSpot> spots) {
        final var vehicleSizeMap = Map.<VehicleType, Size>of(new Car(), MEDIUM,
            new Bike(), SMALL,
            new Truck(), LARGE);
        final var strictSizeMatchingParkingStrategy = new StrictSizeMatchingParkingStrategy(vehicleSizeMap);

        var mayBeParkingSpot = strictSizeMatchingParkingStrategy.findOneFor(vehicle, spots);

        assertInstanceOf(ParkingResult.NoOpenParkingSpot.class, mayBeParkingSpot);
    }

    @ParameterizedTest
    @MethodSource(value = "availableParameters")
    void returnAvailableParkingSpot(Vehicle vehicle, Set<ParkingSpot> spots) {
        final var vehicleSizeMap = Map.<VehicleType, Size>of(new Car(), MEDIUM,
            new Bike(), SMALL,
            new Truck(), LARGE);
        final var strictSizeMatchingParkingStrategy = new StrictSizeMatchingParkingStrategy(vehicleSizeMap);

        var mayBeParkingSpot = strictSizeMatchingParkingStrategy.findOneFor(vehicle, spots);

        assertInstanceOf(ParkingResult.Success.class, mayBeParkingSpot);
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
