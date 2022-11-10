package org.sahaj.calculators;

import org.sahaj.common.FeeResult;
import org.sahaj.common.FeeResult.Success;

import java.math.BigDecimal;

public class ParkingFeeCalculatorWithDiscount implements ParkingFeeCalculator {

    private final ParkingFeeBySpotSizeCalculator parkingFeeBySpotSizeCalculator;
    private final VoucherDiscountCalculator voucherDiscountCalculator;

    public ParkingFeeCalculatorWithDiscount(ParkingFeeBySpotSizeCalculator parkingFeeBySpotSizeCalculator,
        VoucherDiscountCalculator voucherDiscountCalculator) {
        this.parkingFeeBySpotSizeCalculator = parkingFeeBySpotSizeCalculator;
        this.voucherDiscountCalculator = voucherDiscountCalculator;
    }

    @Override
    public FeeResult<BigDecimal> feeFor(ParkingToken token) {
        final var feeRes = parkingFeeBySpotSizeCalculator.feeFor(token);
        if (feeRes instanceof FeeResult.Success<BigDecimal> success && token.getVoucherType().isPresent()) {
            var discount = voucherDiscountCalculator.discountFrom(success.value(), token.voucherType());
            return new Success<>(success.value().subtract(discount));
        }
        return feeRes;
    }
}
