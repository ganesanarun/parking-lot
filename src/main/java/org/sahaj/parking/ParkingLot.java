package org.sahaj.parking;

import org.sahaj.common.Allocation;
import org.sahaj.common.FeeResult.InvalidRangeError;
import org.sahaj.common.FeeResult.SpotSizeNotConfiguredError;
import org.sahaj.common.FeeResult.Success;
import org.sahaj.calculators.ParkingFeeCalculator;
import org.sahaj.calculators.ParkingToken;
import org.sahaj.common.ParkingResult;
import org.sahaj.common.ParkingResult.NoOpenParkingSpot;
import org.sahaj.common.ParkingResult.UnsupportedVehicle;
import org.sahaj.common.ParkingTicket;
import org.sahaj.common.Receipt;
import org.sahaj.common.Vehicle;
import org.sahaj.parking.UnParkingResult.EmptyParkingSpot;
import org.sahaj.parking.UnParkingResult.FailedConfigurationError;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public final class ParkingLot {

    private final ParkingFloor parkingFloor;

    private final ParkingFeeCalculator parkingFeeCalculator;

    public ParkingLot(ParkingFloor parkingFloor, ParkingFeeCalculator parkingFeeCalculator) {
        this.parkingFloor = parkingFloor;
        this.parkingFeeCalculator = parkingFeeCalculator;
    }

    ParkingResult<ParkingTicket> parkThis(Vehicle vehicle) {
        final var maybeAllocation = parkingFloor.parkThis(vehicle);
        return switch (maybeAllocation) {
            case NoOpenParkingSpot<?> ignored -> new NoOpenParkingSpot<>();
            case UnsupportedVehicle<?> ignored -> new UnsupportedVehicle<>();
            case ParkingResult.Success<Allocation> success -> {
                final var parkingTicket = new ParkingTicket(success.value().parkingSpot().number(),
                    ZonedDateTime.now(),
                    success.value().number(),
                    success.value().parkingSpot().size());
                yield new ParkingResult.Success<>(parkingTicket);
            }
        };
    }

    UnParkingResult<Receipt> unParkWith(ParkingTicket parkingTicket) {
        final var b = parkingFloor.unParkWith(parkingTicket);
        if (!b) {
            return new EmptyParkingSpot<>();
        }
        final var exitTime = ZonedDateTime.now();
        final var feeResult = parkingFeeCalculator.feeFor(new ParkingToken(parkingTicket.entryTime(),
            exitTime,
            parkingTicket.size()));
        return switch (feeResult) {
            case Success<BigDecimal> success -> new UnParkingResult.Success<>(new Receipt(parkingTicket.entryTime(),
                exitTime,
                success.value()));
            case SpotSizeNotConfiguredError<BigDecimal> ignored -> new FailedConfigurationError<>();
            case InvalidRangeError<BigDecimal> ignored -> new FailedConfigurationError<>();
        };
    }
}
