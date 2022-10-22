package org.sahaj.calculators;

import org.sahaj.common.FeeResult;
import org.sahaj.common.FeeResult.InvalidRangeError;
import org.sahaj.common.FeeResult.Success;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class HourlyRangeParkingHourFeeCalculator implements ParkingHourFeeCalculator {

    private final BiFunction<ParkingHour, Range, ParkingHour> parkingHourRangeParkingHourBiFunction;
    private final BiFunction<FeeResult<BigDecimal>, FeeResult<BigDecimal>, FeeResult<BigDecimal>> resultProducer;
    private final ParkingHourFeeCalculator next;
    private final Range range;
    private final BigDecimal feeForRange;

    private HourlyRangeParkingHourFeeCalculator(Range range,
        BigDecimal feeForRange,
        BiFunction<ParkingHour, Range, ParkingHour> parkingHourRangeParkingHourBiFunction,
        BiFunction<FeeResult<BigDecimal>, FeeResult<BigDecimal>, FeeResult<BigDecimal>> resultProducer,
        ParkingHourFeeCalculator next) {
        this.feeForRange = feeForRange;
        this.parkingHourRangeParkingHourBiFunction = parkingHourRangeParkingHourBiFunction;
        this.resultProducer = resultProducer;
        this.next = next;
        this.range = range;
    }

    @Override
    public FeeResult<BigDecimal> calculate(ParkingHour parkingHour) {
        if (parkingHour.beyond(this.range)) {
            return resultProducer.apply(
                new Success<>(feeForRange),
                next.calculate(parkingHourRangeParkingHourBiFunction.apply(parkingHour, range)));
        }
        return parkingHour.between(this.range)
            ? new Success<>(feeForRange)
            : new InvalidRangeError<>();
    }

    public static HourlyRangeParkingHourFeeCalculator ofWithIdentical(Range range,
        BigDecimal feeForRange,
        ParkingHourFeeCalculator next) {
        checkNull(range, feeForRange, next);
        return new HourlyRangeParkingHourFeeCalculator(range,
            feeForRange,
            (parkingHour, r) -> parkingHour,
            (result1, result2) -> result2,
            next);
    }

    public static HourlyRangeParkingHourFeeCalculator ofWithSum(Range range,
        BigDecimal feeForRange,
        BiFunction<ParkingHour, Range, ParkingHour> parkingHourRangeParkingHourBiFunction,
        ParkingHourFeeCalculator next) {
        checkNull(range, feeForRange, next);
        return new HourlyRangeParkingHourFeeCalculator(range,
            feeForRange,
            parkingHourRangeParkingHourBiFunction,
            (result1, result2) -> {
                final var success1 = (Success<BigDecimal>) result1;
                final var success2 = (Success<BigDecimal>) result2;
                return new Success<>(success1.value().add(success2.value()));
            },
            next);
    }

    private static void checkNull(Range range,
        BigDecimal feePerDuration,
        ParkingHourFeeCalculator next) {
        checkNull(range, "Range");
        checkNull(feePerDuration, "Parking fee for duration");
        checkNull(next, "Next parking fee calculator");
    }

    private static <T> void checkNull(T obj, String paramName) {
        if (obj == null) {
            throw new IllegalArgumentException(String.format("%s must not be null", paramName));
        }
    }
}
