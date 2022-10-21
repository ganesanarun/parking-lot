package org.sahaj.calculators;

import org.sahaj.FeeResult;
import org.sahaj.FeeResult.SpotSizeNotConfiguredError;
import org.sahaj.Size;

import java.math.BigDecimal;
import java.util.Map;

public class ParkingFeeBySpotSizeCalculator implements ParkingFeeCalculator {

    private final Map<Size, ParkingHourFeeCalculator> feeCalculatorMap;

    public ParkingFeeBySpotSizeCalculator(Map<Size, ParkingHourFeeCalculator> feeCalculatorMap) {
        this.feeCalculatorMap = feeCalculatorMap;
    }

    @Override
    public FeeResult<BigDecimal> feeFor(ParkingToken token) {
        if(!feeCalculatorMap.containsKey(token.size())) {
            return new SpotSizeNotConfiguredError<>(token.size());
        }
        final var parkingHourFeeCalculator = feeCalculatorMap.get(token.size());
        return parkingHourFeeCalculator.calculate(ParkingHour.from(token.entryTime(), token.exitTime()));
    }
}
