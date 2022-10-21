package org.sahaj.calculators;

import org.sahaj.FeeResult;

import java.math.BigDecimal;

@FunctionalInterface
public interface ParkingFeeCalculator {
    FeeResult<BigDecimal> feeFor(ParkingToken token);
}
