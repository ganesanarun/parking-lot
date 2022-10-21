package org.sahaj.calculators;

import org.sahaj.FeeResult;

import java.math.BigDecimal;

@FunctionalInterface
public interface ParkingHourFeeCalculator {

    FeeResult<BigDecimal> calculate(ParkingHour parkingHour);
}