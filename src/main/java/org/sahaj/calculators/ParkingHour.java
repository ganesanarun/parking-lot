package org.sahaj.calculators;

import com.google.common.primitives.UnsignedInteger;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.lang.String.format;

public record ParkingHour(UnsignedInteger hours, UnsignedInteger minutes) {

    public static long roundUpHours(ParkingHour parkingHour) {
        long augend = parkingHour.minutes.longValue() > 0 ? 1 : 0;
        return parkingHour.hours.longValue() + augend;
    }

    public ParkingHour subtractHours(long subtrahend) {
        if (roundUpHours(this) < subtrahend) {
            throw new ArithmeticException(format("Can not subtract %s hours from %s rounded up hours",
                subtrahend,
                roundUpHours(this)));
        }
        return roundUpHours(this) == subtrahend
            ? empty()
            : new ParkingHour(hours.minus(UnsignedInteger.valueOf(subtrahend)), minutes);
    }

    public static ParkingHour empty() {
        return new ParkingHour(UnsignedInteger.ZERO, UnsignedInteger.ZERO);
    }

    public static ParkingHour withHours(long hours) {
        return new ParkingHour(UnsignedInteger.valueOf(hours), UnsignedInteger.ZERO);
    }

    public static ParkingHour from(ZonedDateTime start, ZonedDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start and end must not be null");
        }
        final var totalMinutes = ChronoUnit.MINUTES.between(start, end);
        if (totalMinutes < 0) {
            throw new IllegalArgumentException("start time should be lesser than end time");
        }
        var hours = totalMinutes / 60;
        var minutes = totalMinutes % 60;
        return new ParkingHour(UnsignedInteger.valueOf(hours), UnsignedInteger.valueOf(minutes));
    }

     public boolean between(Range range) {
        return range.getFromHour() < roundUpHours(this)
            && range.getToHour() >= roundUpHours(this);
    }

    public boolean beyond(Range range) {
        return this.hours.longValue() > range.getToHour();
    }
}

