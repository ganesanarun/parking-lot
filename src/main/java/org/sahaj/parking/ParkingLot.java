package org.sahaj.parking;

import org.sahaj.common.FeeResult.InvalidRangeError;
import org.sahaj.common.FeeResult.SpotSizeNotConfiguredError;
import org.sahaj.common.FeeResult.Success;
import org.sahaj.calculators.ParkingFeeCalculator;
import org.sahaj.calculators.ParkingToken;
import org.sahaj.common.ParkingTicket;
import org.sahaj.common.Receipt;
import org.sahaj.common.Vehicle;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.Optional.empty;

public final class ParkingLot {

    private final ParkingFloor parkingFloor;

    private final ParkingFeeCalculator parkingFeeCalculator;

    public ParkingLot(ParkingFloor parkingFloor, ParkingFeeCalculator parkingFeeCalculator) {
        this.parkingFloor = parkingFloor;
        this.parkingFeeCalculator = parkingFeeCalculator;
    }

    // ParkResult -> Success<ParkingTicket>, UnsupportedVehicle, NoOpenParkingSpot
    Optional<ParkingTicket> parkThis(Vehicle vehicle) {
        final var maybeAllocation = parkingFloor.parkThis(vehicle);
        if (maybeAllocation.isEmpty()) {
            return empty();
        }
        return Optional.of(new ParkingTicket(maybeAllocation.get().parkingSpot().number(),
            ZonedDateTime.now(),
            maybeAllocation.get().number(),
            maybeAllocation.get().parkingSpot().size()));
    }

    // UnParkResult -> Success<Receipt>, EmptyParkingSpot, FailedToGenerateReceipt
    Optional<Receipt> unParkWith(ParkingTicket parkingTicket) {
        // parkingFloor number as well
        final var b = parkingFloor.unParkWith(parkingTicket);
        if (!b) {
            return empty();
        }
        final var exitTime = ZonedDateTime.now();
        final var feeResult = parkingFeeCalculator.feeFor(new ParkingToken(parkingTicket.entryTime(),
            exitTime,
            parkingTicket.size()));
        return switch (feeResult) {
            case Success<BigDecimal> success -> Optional.of(new Receipt(parkingTicket.entryTime(),
                exitTime,
                success.value()));
            case SpotSizeNotConfiguredError<BigDecimal> ignored -> Optional.empty();
            case InvalidRangeError<BigDecimal> ignored -> Optional.empty();
        };
    }
}
