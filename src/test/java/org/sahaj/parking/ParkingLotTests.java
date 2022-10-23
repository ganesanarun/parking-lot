package org.sahaj.parking;

import org.junit.jupiter.api.BeforeEach;
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
import org.sahaj.common.VehicleType.Bike;
import org.sahaj.common.VehicleType.Car;
import org.sahaj.common.VehicleType.Truck;
import org.sahaj.calculators.FixedHourlyParkingHourFeeCalculator;
import org.sahaj.calculators.ParkingFeeBySpotSizeCalculator;
import org.sahaj.calculators.ParkingFeeCalculator;
import org.sahaj.calculators.ParkingHourFeeCalculator;
import org.sahaj.calculators.PerDayParkingHourFeeCalculator;
import org.sahaj.calculators.Range;
import org.sahaj.strategys.ParkingSpotAllocationStrategy;
import org.sahaj.strategys.StrictSizeMatchingParkingStrategy;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sahaj.common.Size.LARGE;
import static org.sahaj.common.Size.MEDIUM;
import static org.sahaj.common.Size.SMALL;
import static org.sahaj.calculators.HourlyRangeParkingHourFeeCalculator.ofWithIdentical;
import static org.sahaj.calculators.HourlyRangeParkingHourFeeCalculator.ofWithSum;

public class ParkingLotTests {


    @Nested
    class StadiumParkingLot {

        ParkingFeeCalculator parkingFeeBySpotSizeCalculator;

        Set<ParkingSpot> parkingSpots;

        ParkingSpotAllocationStrategy parkingSpotAllocationStrategy = new StrictSizeMatchingParkingStrategy(
            Map.of(new Car(), MEDIUM, new Bike(), SMALL));

        @BeforeEach
        void setup() {
            final var bikeSpot1 = new ParkingSpot("S1", SMALL);
            final var bikeSpot2 = new ParkingSpot("S2", SMALL);
            final var bikeSpot3 = new ParkingSpot("S3", SMALL);
            final var carSpot1 = new ParkingSpot("M1", MEDIUM);
            final var carSpot2 = new ParkingSpot("M2", MEDIUM);
            final var carSpot3 = new ParkingSpot("M3", MEDIUM);
            parkingSpots = Set.of(bikeSpot3, bikeSpot2, bikeSpot1, carSpot3, carSpot2, carSpot1);
            final var bikeEndRange = ofWithSum(Range.from(4, 12),
                valueOf(60),
                new SubtractToRangeFromHour(),
                new FixedHourlyParkingHourFeeCalculator(valueOf(100)));
            final var bikeFeeCalculator = ofWithSum(Range.from(0, 4),
                valueOf(30),
                new IdenticalParkingHour(),
                bikeEndRange);
            final var carEndRange = ofWithSum(Range.from(4, 12),
                valueOf(120),
                new SubtractToRangeFromHour(),
                new FixedHourlyParkingHourFeeCalculator(valueOf(200)));
            final var carFeeCalculator = ofWithSum(Range.from(0, 4),
                valueOf(60),
                new IdenticalParkingHour(),
                carEndRange);
            final var calculatorMap = Map.<Size, ParkingHourFeeCalculator>of(
                SMALL, bikeFeeCalculator,
                MEDIUM, carFeeCalculator);
            parkingFeeBySpotSizeCalculator = new ParkingFeeBySpotSizeCalculator(calculatorMap);
        }

        @Test
        void unableToParkTruck() {
            final var parkingFloor = new ParkingFloor(parkingSpots, parkingSpotAllocationStrategy, () -> "");
            ParkingLot stadiumParkingLot = new ParkingLot(parkingFloor, parkingFeeBySpotSizeCalculator);

            final var maybeParkingTicket = stadiumParkingLot.parkThis(new Vehicle(new Truck()));

            assertTrue(maybeParkingTicket.isEmpty());
        }

        @ParameterizedTest
        @MethodSource(value = "parkArguments")
        void ableToPark(Vehicle vehicle) {
            final var ticketNumberGenerator = new InMemoryTicketNumberGenerator(1, () -> "");
            final var parkingFloor = new ParkingFloor(
                parkingSpots,
                parkingSpotAllocationStrategy,
                ticketNumberGenerator);
            ParkingLot airportParkingLot = new ParkingLot(parkingFloor, parkingFeeBySpotSizeCalculator);

            final var maybeParkingTicket = airportParkingLot.parkThis(vehicle);

            assertTrue(maybeParkingTicket.isPresent());
        }

        @ParameterizedTest(name = "[{index}] {3}")
        @MethodSource(value = "unParkArguments")
        void successfulUnParking(ParkingTicket parkingTicket,
            Allocation allocation,
            BigDecimal expectedResult,
            String scenario) {
            final var ticketNumberGenerator = new InMemoryTicketNumberGenerator(1, () -> "");
            final var parkingFloor = new ParkingFloor(parkingSpots,
                Set.of(allocation),
                parkingSpotAllocationStrategy,
                ticketNumberGenerator);
            ParkingLot airportParkingLot = new ParkingLot(parkingFloor, parkingFeeBySpotSizeCalculator);

            final var receipt = airportParkingLot.unParkWith(parkingTicket);

            assertTrue(receipt.isPresent());
            assertEquals(expectedResult, receipt.get().value());
        }

        static Stream<Arguments> parkArguments() {
            return Stream.of(Arguments.of(new Vehicle(new Bike())), Arguments.of(new Vehicle(new Car())));
        }

        static Stream<Arguments> unParkArguments() {
            final var firstBikeEntryTime = ZonedDateTime.now().minusHours(3).minusMinutes(40);
            final var bikeParkedLessThan4Hour = new Allocation("001", new ParkingSpot("S4", SMALL),
                firstBikeEntryTime,
                new Vehicle(new Bike()));
            final var parkingTicket1 = new ParkingTicket("S4", firstBikeEntryTime, "001", SMALL);

            final var secondBikeEntryTime = ZonedDateTime.now().minusMinutes(50).minusHours(14);
            final var bikeParkedAlmost15Hours = new Allocation("002", new ParkingSpot("S5", SMALL),
                secondBikeEntryTime,
                new Vehicle(new Bike()));
            final var parkingTicket2 = new ParkingTicket("S5", secondBikeEntryTime, "002", SMALL);

            final var firstCarEntryTime = ZonedDateTime.now().minusMinutes(30).minusHours(11);
            final var carParkedLessThan12Hours = new Allocation("004", new ParkingSpot("M4", MEDIUM),
                firstCarEntryTime,
                new Vehicle(new Car()));
            final var carParkingTicket1 = new ParkingTicket("M4", firstCarEntryTime, "004", MEDIUM);

            final var secondCarEntryTime = ZonedDateTime.now().minusHours(13).minusMinutes(5);
            final var carParkedFor13Hours5Mins = new Allocation("005", new ParkingSpot("M5", MEDIUM),
                secondCarEntryTime,
                new Vehicle(new Car()));
            final var carParkingTicket2 = new ParkingTicket("M5", secondCarEntryTime, "005", MEDIUM);

            return Stream.of(
                Arguments.of(parkingTicket1, bikeParkedLessThan4Hour, valueOf(30),
                    "Motorcycle parked for 3 hours and 40 mins. Fees: 30"),
                Arguments.of(parkingTicket2, bikeParkedAlmost15Hours, valueOf(390),
                    "Motorcycle parked for 14 hours and 59 mins. Fees: 390"),
                Arguments.of(carParkingTicket1, carParkedLessThan12Hours, valueOf(180),
                    "Electric SUV parked for 11 hours and 30 mins. Fees: 180"),
                Arguments.of(carParkingTicket2, carParkedFor13Hours5Mins, valueOf(580),
                    "SUV parked for 13 hours and 5 mins. Fees: 580."));
        }
    }

    @Nested
    class AirportParkingLot {

        ParkingFeeCalculator parkingFeeBySpotSizeCalculator;

        Set<ParkingSpot> parkingSpots;

        ParkingSpotAllocationStrategy parkingSpotAllocationStrategy = new StrictSizeMatchingParkingStrategy(
            Map.of(new Car(), MEDIUM, new Bike(), SMALL));

        @BeforeEach
        void setup() {
            final var bikeSpot1 = new ParkingSpot("S1", SMALL);
            final var bikeSpot2 = new ParkingSpot("S2", SMALL);
            final var bikeSpot3 = new ParkingSpot("S3", SMALL);
            final var carSpot1 = new ParkingSpot("M1", MEDIUM);
            final var carSpot2 = new ParkingSpot("M2", MEDIUM);
            final var carSpot3 = new ParkingSpot("M3", MEDIUM);
            parkingSpots = Set.of(bikeSpot3, bikeSpot2, bikeSpot1, carSpot3, carSpot2, carSpot1);

            final var bikeEndRange = ofWithIdentical(Range.from(8, 24),
                valueOf(60),
                new PerDayParkingHourFeeCalculator(valueOf(80)));
            final var secondRange = ofWithIdentical(Range.from(1, 8),
                valueOf(40),
                bikeEndRange);
            final var bikeFeeCalculator = ofWithIdentical(Range.from(0, 1),
                valueOf(0),
                secondRange);
            final var carEndRange = ofWithIdentical(Range.from(12, 24),
                valueOf(80),
                new PerDayParkingHourFeeCalculator(valueOf(100)));
            final var carFeeCalculator = ofWithIdentical(Range.from(0, 12),
                valueOf(60),
                carEndRange);
            final var calculatorMap = Map.<Size, ParkingHourFeeCalculator>of(
                SMALL, bikeFeeCalculator,
                MEDIUM, carFeeCalculator);
            parkingFeeBySpotSizeCalculator = new ParkingFeeBySpotSizeCalculator(calculatorMap);

        }

        @Test
        void unableToParkTruck() {
            final var parkingFloor = new ParkingFloor(
                parkingSpots,
                parkingSpotAllocationStrategy, () -> "");
            ParkingLot airportParkingLot = new ParkingLot(parkingFloor, parkingFeeBySpotSizeCalculator);

            final var maybeParkingTicket = airportParkingLot.parkThis(new Vehicle(new Truck()));

            assertTrue(maybeParkingTicket.isEmpty());
        }

        @ParameterizedTest
        @MethodSource(value = "parkArguments")
        void ableToPark(Vehicle vehicle) {
            final var parkingFloor = new ParkingFloor(
                parkingSpots,
                parkingSpotAllocationStrategy,
                () -> "");
            ParkingLot airportParkingLot = new ParkingLot(parkingFloor, parkingFeeBySpotSizeCalculator);

            final var maybeParkingTicket = airportParkingLot.parkThis(vehicle);

            assertTrue(maybeParkingTicket.isPresent());
        }

        @ParameterizedTest(name = "[{index}] {3}")
        @MethodSource(value = "unParkArguments")
        void successfulUnParking(ParkingTicket parkingTicket,
            Allocation allocation,
            BigDecimal expectedResult,
            String scenario) {
            final var ticketNumberGenerator = new InMemoryTicketNumberGenerator(1, () -> "");
            final var parkingFloor = new ParkingFloor(parkingSpots,
                Set.of(allocation),
                parkingSpotAllocationStrategy,
                ticketNumberGenerator);
            ParkingLot airportParkingLot = new ParkingLot(parkingFloor, parkingFeeBySpotSizeCalculator);

            final var receipt = airportParkingLot.unParkWith(parkingTicket);

            assertTrue(receipt.isPresent());
            assertEquals(expectedResult, receipt.get().value());
        }

        static Stream<Arguments> parkArguments() {
            return Stream.of(Arguments.of(new Vehicle(new Bike())), Arguments.of(new Vehicle(new Car())));
        }

        static Stream<Arguments> unParkArguments() {
            final var firstBikeEntryTime = ZonedDateTime.now().minusMinutes(50);
            final var bikeParkedLessThanAnHour = new Allocation("001", new ParkingSpot("S4", SMALL),
                firstBikeEntryTime,
                new Vehicle(new Bike()));
            final var parkingTicket1 = new ParkingTicket("S4", firstBikeEntryTime, "001", SMALL);

            final var secondBikeEntryTime = ZonedDateTime.now().minusMinutes(50).minusHours(14);
            final var bikeParkedAlmost15Hours = new Allocation("002", new ParkingSpot("S5", SMALL),
                secondBikeEntryTime,
                new Vehicle(new Bike()));
            final var parkingTicket2 = new ParkingTicket("S5", secondBikeEntryTime, "002", SMALL);

            final var ThirdBikeEntryTime = ZonedDateTime.now().minusHours(12).minusDays(1);
            final var ThirdBikeParkedAlmost2Days = new Allocation("003", new ParkingSpot("S6", SMALL),
                ThirdBikeEntryTime,
                new Vehicle(new Bike()));
            final var parkingTicket3 = new ParkingTicket("S6", ThirdBikeEntryTime, "003", SMALL);

            final var firstCarEntryTime = ZonedDateTime.now().minusMinutes(50);
            final var carParkedLessThanAnHour = new Allocation("004", new ParkingSpot("M4", MEDIUM),
                firstCarEntryTime,
                new Vehicle(new Car()));
            final var carParkingTicket1 = new ParkingTicket("M4", firstCarEntryTime, "004", MEDIUM);

            final var secondCarEntryTime = ZonedDateTime.now().minusHours(23).minusMinutes(57);
            final var carParkedLessThanADay = new Allocation("005", new ParkingSpot("M5", MEDIUM),
                secondCarEntryTime,
                new Vehicle(new Car()));
            final var carParkingTicket2 = new ParkingTicket("M5", secondCarEntryTime, "005", MEDIUM);

            final var thirdCarEntryTime = ZonedDateTime.now().minusDays(3).minusHours(1);
            final var carParkedLessAlmost4Days = new Allocation("006", new ParkingSpot("M6", MEDIUM),
                thirdCarEntryTime,
                new Vehicle(new Car()));
            final var carParkingTicket3 = new ParkingTicket("M6", thirdCarEntryTime, "006", MEDIUM);

            return Stream.of(
                Arguments.of(parkingTicket1, bikeParkedLessThanAnHour, ZERO, "Motorcycle parked for 55 mins. Fees: 0"),
                Arguments.of(parkingTicket2, bikeParkedAlmost15Hours, valueOf(60),
                    "Motorcycle parked for 14 hours and 59 mins. Fees: 60"),
                Arguments.of(parkingTicket3, ThirdBikeParkedAlmost2Days, valueOf(160),
                    "Motorcycle parked for 1 day and 12 hours. Fees: 160"),
                Arguments.of(carParkingTicket1, carParkedLessThanAnHour, valueOf(60),
                    "Car parked for 50 mins. Fees: 60"),
                Arguments.of(carParkingTicket2, carParkedLessThanADay, valueOf(80),
                    "SUV parked for 23 hours and 59 mins. Fees: 80"),
                Arguments.of(carParkingTicket3, carParkedLessAlmost4Days, valueOf(400),
                    "Car parked for 3 days and 1 hour. Fees: 400"));
        }
    }

    @Nested
    class MallParkingLot {

        ParkingFeeCalculator parkingFeeBySpotSizeCalculator;

        Set<ParkingSpot> parkingSpots;

        ParkingSpotAllocationStrategy parkingSpotAllocationStrategy = new StrictSizeMatchingParkingStrategy(
            Map.of(new Car(), MEDIUM, new Bike(), SMALL, new Truck(), LARGE));

        @BeforeEach
        void setup() {
            final var bikeSpot1 = new ParkingSpot("S1", SMALL);
            final var bikeSpot2 = new ParkingSpot("S2", SMALL);
            final var bikeSpot3 = new ParkingSpot("S3", SMALL);
            final var carSpot1 = new ParkingSpot("M1", MEDIUM);
            final var carSpot2 = new ParkingSpot("M2", MEDIUM);
            final var carSpot3 = new ParkingSpot("M3", MEDIUM);
            final var truckSpot1 = new ParkingSpot("L1", LARGE);
            final var truckSpot2 = new ParkingSpot("L2", LARGE);
            final var truckSpot3 = new ParkingSpot("L3", LARGE);
            parkingSpots = Set.of(bikeSpot3,
                bikeSpot2,
                bikeSpot1,
                carSpot3,
                carSpot2,
                carSpot1,
                truckSpot2,
                truckSpot1,
                truckSpot3);
            final var bikeFeeCalculator = new FixedHourlyParkingHourFeeCalculator(BigDecimal.TEN);
            final var carFeeCalculator = new FixedHourlyParkingHourFeeCalculator(BigDecimal.valueOf(20));
            final var truckFeeCalculator = new FixedHourlyParkingHourFeeCalculator(BigDecimal.valueOf(50));
            final var calculatorMap = Map.<Size, ParkingHourFeeCalculator>of(
                SMALL, bikeFeeCalculator,
                MEDIUM, carFeeCalculator,
                LARGE, truckFeeCalculator);
            parkingFeeBySpotSizeCalculator = new ParkingFeeBySpotSizeCalculator(calculatorMap);
        }

        @ParameterizedTest
        @MethodSource(value = "parkArguments")
        void ableToPark(Vehicle vehicle) {
            final var ticketNumberGenerator = new InMemoryTicketNumberGenerator(1, () -> "");
            final var parkingFloor = new ParkingFloor(
                parkingSpots,
                parkingSpotAllocationStrategy,
                ticketNumberGenerator);
            ParkingLot mallParkingLot = new ParkingLot(parkingFloor, parkingFeeBySpotSizeCalculator);

            final var maybeParkingTicket = mallParkingLot.parkThis(vehicle);

            assertTrue(maybeParkingTicket.isPresent());
        }

        @ParameterizedTest(name = "[{index}] {3}")
        @MethodSource(value = "unParkArguments")
        void successfulUnParking(ParkingTicket parkingTicket,
            Allocation allocation,
            BigDecimal expectedResult,
            String scenario) {
            final var ticketNumberGenerator = new InMemoryTicketNumberGenerator(1, () -> "");
            final var parkingFloor = new ParkingFloor(parkingSpots,
                Set.of(allocation),
                parkingSpotAllocationStrategy,
                ticketNumberGenerator);
            ParkingLot mallParkingLot = new ParkingLot(parkingFloor, parkingFeeBySpotSizeCalculator);

            final var receipt = mallParkingLot.unParkWith(parkingTicket);

            assertTrue(receipt.isPresent());
            assertEquals(expectedResult, receipt.get().value());
        }

        static Stream<Arguments> parkArguments() {
            return Stream.of(Arguments.of(new Vehicle(new Bike())),
                Arguments.of(new Vehicle(new Car())),
                Arguments.of(new Vehicle(new Truck())));
        }

        static Stream<Arguments> unParkArguments() {
            final var firstBikeEntryTime = ZonedDateTime.now().minusHours(3).minusMinutes(40);
            final var bikeParkedLessThan4Hour = new Allocation("001", new ParkingSpot("S4", SMALL),
                firstBikeEntryTime,
                new Vehicle(new Bike()));
            final var bikeParkingTicket = new ParkingTicket("S4", firstBikeEntryTime, "001", SMALL);

            final var firstCarEntryTime = ZonedDateTime.now().minusMinutes(1).minusHours(6);
            final var carParkedLessThan7Hours = new Allocation("004", new ParkingSpot("M4", MEDIUM),
                firstCarEntryTime,
                new Vehicle(new Car()));
            final var carParkingTicket = new ParkingTicket("M4", firstCarEntryTime, "004", MEDIUM);

            final var firstTruckEntryTime = ZonedDateTime.now().minusHours(1).minusMinutes(55);
            final var truckParkedLessThan2Hours = new Allocation("005", new ParkingSpot("L4", LARGE),
                firstTruckEntryTime,
                new Vehicle(new Truck()));
            final var truckParkingTicket = new ParkingTicket("L4", firstTruckEntryTime, "005", LARGE);

            return Stream.of(
                Arguments.of(bikeParkingTicket, bikeParkedLessThan4Hour, valueOf(40),
                    "Motorcycle parked for 3 hours and 30 mins. Fees: 40"),
                Arguments.of(carParkingTicket, carParkedLessThan7Hours, valueOf(140),
                    "Car parked for 6 hours and 1 min. Fees: 140"),
                Arguments.of(truckParkingTicket, truckParkedLessThan2Hours, valueOf(100),
                    "Truck parked for 1 hour and 59 mins. Fees: 100"));
        }
    }

}
