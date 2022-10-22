package org.sahaj.calculators;

import org.sahaj.common.FeeResult;

import java.math.BigDecimal;

@FunctionalInterface
public interface ParkingFeeCalculator {
    FeeResult<BigDecimal> feeFor(ParkingToken token);
}
