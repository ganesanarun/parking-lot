package org.sahaj.calculators;

import org.sahaj.common.FeeResult;
import org.sahaj.common.FeeResult.Success;

import java.math.BigDecimal;

public class PerDayParkingHourFeeCalculator implements ParkingHourFeeCalculator {

    private final BigDecimal dailyFee;

    public PerDayParkingHourFeeCalculator(BigDecimal dailyFee) {
        this.dailyFee = dailyFee;
    }

    @Override
    public FeeResult<BigDecimal> calculate(ParkingHour parkingHour) {
        return new Success<>(dailyFee.multiply(daysFrom(ParkingHour.roundUpHours(parkingHour))));
    }

    static BigDecimal daysFrom(long totalHours) {
        var days = totalHours / 24;
        return totalHours % 24 == 0 ? BigDecimal.valueOf(days) : BigDecimal.valueOf(days + 1);
    }
}
