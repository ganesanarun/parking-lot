package org.sahaj.calculators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class VoucherDiscountCalculator {


    private final Map<VoucherType, Integer> voucherToPercentage;

    public VoucherDiscountCalculator(Map<VoucherType, Integer> voucherToPercentage) {
        this.voucherToPercentage = voucherToPercentage;
    }

    public BigDecimal discountFrom(BigDecimal parkingFee, VoucherType type) {
        if (!voucherToPercentage.containsKey(type)) {
            return BigDecimal.ZERO;
        }
        final var percentage = voucherToPercentage.get(type);
        final var divisor = 100 / percentage;
        return parkingFee.divide(BigDecimal.valueOf(divisor), RoundingMode.UNNECESSARY);
    }
}
