package org.sahaj.parking;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sahaj.common.Allocation;
import org.sahaj.common.ParkingSpot;
import org.sahaj.common.ParkingTicket;
import org.sahaj.common.Size;
import org.sahaj.common.Vehicle;
import org.sahaj.common.VehicleType;
import org.sahaj.common.VehicleType.Bike;
import org.sahaj.common.VehicleType.Car;
import org.sahaj.common.VehicleType.Truck;
import org.sahaj.strategys.ParkingSpotAllocationStrategy;
import org.sahaj.strategys.StrictSizeMatchingParkingStrategy;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sahaj.common.Size.LARGE;
import static org.sahaj.common.Size.MEDIUM;
import static org.sahaj.common.Size.SMALL;

public class ParkingFloorTests {

    @Nested
    class ParkingTests {

        @ParameterizedTest(name = "[{index}] {3}")
        @MethodSource(value = "availableParkingSpots")
        void returnParkingTicketWhenSpotIsAvailable(Set<ParkingSpot> spots,
            Vehicle vehicleToPark,
            ParkingSpot parkingSpotToBeUsed,
            String scenario) {
            final var vehicleTypeSizeMap = Map.<VehicleType, Size>of(new Bike(), SMALL,
                new Car(), MEDIUM,
                new Truck(), LARGE);
            final var parkingFloor = new ParkingFloor(spots, new StrictSizeMatchingParkingStrategy(vehicleTypeSizeMap));

            final var maybeParkResult = parkingFloor.parkThis(vehicleToPark);

            assertTrue(maybeParkResult.isPresent());
            assertEquals(maybeParkResult.get().parkingSpot(), parkingSpotToBeUsed);
        }

        @ParameterizedTest(name = "[{index}] {2}")
        @MethodSource(value = "unAvailableParkingSpots")
        void returnEmptyResultIfParkingFloorIsFull(Set<ParkingSpot> spots,
            Vehicle vehicleToPark,
            String scenario) {
            final var vehicleTypeSizeMap = Map.<VehicleType, Size>of(new Bike(), SMALL,
                new Car(), MEDIUM,
                new Truck(), LARGE);
            final var parkingFloor = new ParkingFloor(spots, new StrictSizeMatchingParkingStrategy(vehicleTypeSizeMap));

            final var maybeParkResult = parkingFloor.parkThis(vehicleToPark);

            assertTrue(maybeParkResult.isEmpty());
        }

        @ParameterizedTest(name = "[{index}] {3}")
        @MethodSource(value = "unSupportedParkingSpots")
        void returnEmptyResultIfParkingIsNotSupportedVehicle(
            ParkingSpotAllocationStrategy allocationStrategy,
            Set<ParkingSpot> spots,
            Vehicle vehicleToPark,
            String scenario) {
            final var vehicleTypeSizeMap = Map.<VehicleType, Size>of(new Bike(), SMALL,
                new Car(), MEDIUM,
                new Truck(), LARGE);
            final var parkingFloor = new ParkingFloor(spots, allocationStrategy);

            final var maybeParkResult = parkingFloor.parkThis(vehicleToPark);

            assertTrue(maybeParkResult.isEmpty());
        }


        static Stream<Arguments> availableParkingSpots() {
            final var bikeSpot = new ParkingSpot("S1", SMALL);
            final var carSpot = new ParkingSpot("M1", MEDIUM);
            final var truckSpot = new ParkingSpot("L1", LARGE);
            final var spots = Set.of(bikeSpot, carSpot, truckSpot);
            return Stream.of(
                Arguments.of(spots, new Vehicle(new Bike()), bikeSpot, "bike parked successfully"),
                Arguments.of(spots, new Vehicle(new Car()), carSpot, "car parked successfully"),
                Arguments.of(spots, new Vehicle(new Truck()), truckSpot, "truck parked successfully")
            );
        }

        static Stream<Arguments> unAvailableParkingSpots() {
            final var bikeSpot = new ParkingSpot("S1", SMALL);
            final var carSpot = new ParkingSpot("M1", MEDIUM);
            final var truckSpot = new ParkingSpot("L1", LARGE);
            return Stream.of(
                Arguments.of(Set.of(carSpot, truckSpot), new Vehicle(new Bike()), "unavailable to park bike"),
                Arguments.of(Set.of(bikeSpot, truckSpot), new Vehicle(new Car()), "unavailable to park car"),
                Arguments.of(Set.of(carSpot, bikeSpot), new Vehicle(new Truck()), "unavailable to park truck")
            );
        }

        static Stream<Arguments> unSupportedParkingSpots() {
            final var bikeSpot = new ParkingSpot("S1", SMALL);
            final var carSpot = new ParkingSpot("M1", MEDIUM);
            final var truckSpot = new ParkingSpot("L1", LARGE);
            final var bikeUnsupportedStrategy = new StrictSizeMatchingParkingStrategy(Map.of(new Car(), MEDIUM,
                new Truck(), LARGE));
            final var carUnsupportedStrategy = new StrictSizeMatchingParkingStrategy(Map.of(new Bike(), SMALL,
                new Truck(), LARGE));
            final var truckUnsupportedStrategy = new StrictSizeMatchingParkingStrategy(Map.of(new Bike(), SMALL,
                new Car(), MEDIUM));
            return Stream.of(
                Arguments.of(bikeUnsupportedStrategy, Set.of(carSpot, truckSpot, bikeSpot), new Vehicle(new Bike()),
                    "unsupported to park bike"),
                Arguments.of(carUnsupportedStrategy, Set.of(bikeSpot, truckSpot, carSpot), new Vehicle(new Car()),
                    "unsupported to park car"),
                Arguments.of(truckUnsupportedStrategy, Set.of(carSpot, bikeSpot, truckSpot), new Vehicle(new Truck()),
                    "unsupported to park truck")
            );
        }
    }

    @Nested
    class UnParkingTests {

        @ParameterizedTest(name = "[{index}] {3}")
        @MethodSource(value = "unParkVehicles")
        void successfullyUnParkAndAllowSimilarVehicleToParkWhenValidTicketNumberGiven(Vehicle vehicleToPark,
            Set<Allocation> allocations,
            ParkingTicket parkingTicket,
            String scenario) {
            final var vehicleTypeSizeMap = Map.<VehicleType, Size>of(new Bike(), SMALL,
                new Car(), MEDIUM,
                new Truck(), LARGE);
            final var parkingFloor = new ParkingFloor(Set.of(),
                allocations,
                new StrictSizeMatchingParkingStrategy(vehicleTypeSizeMap));

            final var allocationBeforeUnParked = parkingFloor.parkThis(vehicleToPark);
            final var unParked = parkingFloor.unParkWith(parkingTicket);
            final var allocationAfterUnParked = parkingFloor.parkThis(vehicleToPark);

            assertTrue(allocationBeforeUnParked.isEmpty());
            assertTrue(unParked);
            assertTrue(allocationAfterUnParked.isPresent());
        }

        @Test
        void returnFailToUnParkWhenInvalidTicketNumberIsGiven() {
             var parkingTicket = new ParkingTicket("A1",
                        ZonedDateTime.now(),
                        "001",
                        SMALL);
            final var parkingFloor = new ParkingFloor(Set.of(),
                Set.of(),
                new StrictSizeMatchingParkingStrategy(Map.of()));

            final var unParked = parkingFloor.unParkWith(parkingTicket);

            assertFalse(unParked);
        }

        private static Stream<Arguments> unParkVehicles() {
            final var entryTime = ZonedDateTime.now();
            return Stream.of(
                Arguments.of(new Vehicle(new Bike()), Set.of(new Allocation("001",
                        new ParkingSpot("B1", SMALL),
                        entryTime,
                        new Vehicle(new Bike()))),
                    new ParkingTicket("A1",
                        entryTime,
                        "001",
                        SMALL),
                    "un-park and park bike"),
                Arguments.of(new Vehicle(new Car()), Set.of(new Allocation("001",
                        new ParkingSpot("C1", MEDIUM),
                        entryTime,
                        new Vehicle(new Car()))),
                    new ParkingTicket("A1",
                        entryTime,
                        "001",
                        MEDIUM),
                    "un-park and park car"),
                Arguments.of(new Vehicle(new Truck()), Set.of(new Allocation("001",
                        new ParkingSpot("T1", LARGE),
                        entryTime,
                        new Vehicle(new Truck()))),
                    new ParkingTicket("A1",
                        entryTime,
                        "001",
                        LARGE),
                    "un-park and park truck")
            );
        }
    }
}
