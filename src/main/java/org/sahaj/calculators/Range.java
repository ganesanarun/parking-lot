package org.sahaj.calculators;

import com.google.common.primitives.UnsignedInteger;

public record Range(UnsignedInteger fromHour, UnsignedInteger toHour) {

    public static Range from(long fromHour, long toHour) {
        checkParameters(fromHour, toHour);
        return new Range(UnsignedInteger.valueOf(fromHour), UnsignedInteger.valueOf(toHour));
    }

    public long getFromHour() {
        return fromHour.longValue();
    }

    public long getToHour() {
        return toHour.longValue();
    }

    private static void checkParameters(long from, long to) {
        if (to < 0 || from < 0) {
            throw new IllegalArgumentException(String.format("from %s or to %s should not be lesser than zero", from, to));
        }

        if (to < from) {
            throw new IllegalArgumentException(String.format("from %s should be lesser than to %s", from, to));
        }

        if (to == from) {
            throw new IllegalArgumentException(String.format("from %s should not be equal to %s", from, to));
        }
    }
}
