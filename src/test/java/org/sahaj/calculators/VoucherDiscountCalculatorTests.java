package org.sahaj.calculators;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VoucherDiscountCalculatorTests {

    @Test
    void returnDiscountForVoucherTypes() {
        final var voucherToPercentage = Map.of(VoucherType.EMPLOYEE, 50,
            VoucherType.STORE_OWNER, 100);
        final var voucherDiscountCalculator = new VoucherDiscountCalculator(voucherToPercentage);
        final var parkingFee = BigDecimal.valueOf(100);


       BigDecimal discountFeeEmployee =  voucherDiscountCalculator.discountFrom(parkingFee, VoucherType.EMPLOYEE);
       BigDecimal discountFeeStoreOwner =  voucherDiscountCalculator.discountFrom(parkingFee, VoucherType.STORE_OWNER);

       assertEquals(discountFeeEmployee, BigDecimal.valueOf(50));
       assertEquals(discountFeeStoreOwner, BigDecimal.valueOf(100));
    }

    @Test
    void returnZeroWhenVoucherTypeIsNotConfigured() {
        final var voucherDiscountCalculator = new VoucherDiscountCalculator(Map.of());
        final var parkingFee = BigDecimal.valueOf(100);

       BigDecimal discountFee =  voucherDiscountCalculator.discountFrom(parkingFee, VoucherType.STORE_OWNER);

       assertEquals(discountFee, BigDecimal.valueOf(0));
    }
}