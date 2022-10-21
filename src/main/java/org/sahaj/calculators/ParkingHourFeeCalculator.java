package org.sahaj.calculators;

import org.sahaj.Result;

import java.math.BigDecimal;

@FunctionalInterface
public interface ParkingHourFeeCalculator {

    Result<BigDecimal> calculate(ParkingHour parkingHour);
}
