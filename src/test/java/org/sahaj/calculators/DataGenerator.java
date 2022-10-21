package org.sahaj.calculators;

import com.google.common.primitives.UnsignedInteger;

public class DataGenerator {

    public static ParkingHour from(long hours, long minutes) {
        return new ParkingHour(UnsignedInteger.valueOf(hours), UnsignedInteger.valueOf(minutes));
    }
}
