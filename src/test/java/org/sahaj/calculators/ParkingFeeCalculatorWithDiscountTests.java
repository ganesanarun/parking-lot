package org.sahaj.calculators;

import org.junit.jupiter.api.Test;
import org.sahaj.common.FeeResult;
import org.sahaj.common.FeeResult.Success;
import org.sahaj.common.Size;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sahaj.calculators.VoucherType.EMPLOYEE;
import static org.sahaj.common.Size.SMALL;

public class ParkingFeeCalculatorWithDiscountTests {

    @Test
    void return50PercentageDiscountedFeeForEmployee() {
        final var parkingFeeBySpotSizeCalculator = new ParkingFeeBySpotSizeCalculator(Map.of(Size.SMALL,
            new FixedHourlyParkingHourFeeCalculator(BigDecimal.TEN)));
        final var parkingFeeCalculatorWithDiscount = new ParkingFeeCalculatorWithDiscount(
            parkingFeeBySpotSizeCalculator,
            new VoucherDiscountCalculator(Map.of(EMPLOYEE, 50)));
        final var entryTime = ZonedDateTime.now().minusHours(1);
        final var exitTime = ZonedDateTime.now();
        final var tokenWithEmployeeVoucher = new ParkingToken(entryTime, exitTime, SMALL, EMPLOYEE);

        final var feeResult = (Success<BigDecimal>) parkingFeeCalculatorWithDiscount.feeFor(tokenWithEmployeeVoucher);

        assertEquals(feeResult.value(), BigDecimal.valueOf(5));
    }

}
