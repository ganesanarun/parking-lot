package org.sahaj.calculators;

import org.sahaj.common.FeeResult;

import java.math.BigDecimal;

@FunctionalInterface
public interface ParkingHourFeeCalculator {

    FeeResult<BigDecimal> calculate(ParkingHour parkingHour);
}
