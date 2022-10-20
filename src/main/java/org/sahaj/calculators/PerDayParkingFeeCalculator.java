package org.sahaj.calculators;

import org.sahaj.Result;
import org.sahaj.Result.Success;

import java.math.BigDecimal;

public class PerDayParkingFeeCalculator implements ParkingFeeCalculator {

    private final BigDecimal dailyFee;

    public PerDayParkingFeeCalculator(BigDecimal dailyFee) {
        this.dailyFee = dailyFee;
    }

    @Override
    public Result<BigDecimal> calculate(ParkingHour parkingHour) {
        return new Success<>(dailyFee.multiply(daysFrom(ParkingHour.roundUpHours(parkingHour))));
    }

    static BigDecimal daysFrom(long totalHours) {
        var days = totalHours / 24;
        return totalHours % 24 == 0 ? BigDecimal.valueOf(days) : BigDecimal.valueOf(days + 1);
    }
}
