package org.sahaj.calculators;

import com.google.common.primitives.UnsignedInteger;

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

    public static ParkingHour from(long hours, long minutes) {
        if (hours < 0) {
            throw new IllegalArgumentException("hours must not be negative");
        }
        if (minutes < 0 || minutes >= 60) {
            throw new IllegalArgumentException("minutes must not be negative or greater than 59");
        }
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

