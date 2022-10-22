package org.sahaj.parking;

import org.sahaj.common.FeeResult;
import org.sahaj.common.FeeResult.Success;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class SumFees implements BiFunction<FeeResult<BigDecimal>, FeeResult<BigDecimal>, FeeResult<BigDecimal>> {

    @Override
    public FeeResult<BigDecimal> apply(FeeResult<BigDecimal> bigDecimalFeeResult,
        FeeResult<BigDecimal> bigDecimalFeeResult2) {
        if (bigDecimalFeeResult instanceof FeeResult.Success<BigDecimal> success1 &&
            bigDecimalFeeResult2 instanceof FeeResult.Success<BigDecimal> success2) {
            return new Success<>(success1.value().add(success2.value()));
        }
        // for now
        return bigDecimalFeeResult;
    }
}
