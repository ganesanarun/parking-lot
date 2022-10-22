package org.sahaj.parking;

import org.sahaj.calculators.ParkingHour;
import org.sahaj.calculators.Range;

import java.util.function.BiFunction;

public class SubtractToRangeFromHour implements BiFunction<ParkingHour, Range, ParkingHour> {

    @Override
    public ParkingHour apply(ParkingHour parkingHour, Range range) {
        return parkingHour.subtractHours(range.getToHour());
    }
}
