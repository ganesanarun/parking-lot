package org.sahaj.calculators;

import org.sahaj.Result;
import org.sahaj.Result.InvalidRangeError;
import org.sahaj.Result.Success;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class HourlyRangeParkingFeeCalculator implements ParkingFeeCalculator {

    private final BiFunction<ParkingHour, Range, ParkingHour> parkingHourRangeParkingHourBiFunction;
    private final BiFunction<Result<BigDecimal>, Result<BigDecimal>, Result<BigDecimal>> resultProducer;
    private final ParkingFeeCalculator next;
    private final Range range;
    private final BigDecimal feeForRange;

    private HourlyRangeParkingFeeCalculator(Range range,
        BigDecimal feeForRange,
        BiFunction<ParkingHour, Range, ParkingHour> parkingHourRangeParkingHourBiFunction,
        BiFunction<Result<BigDecimal>, Result<BigDecimal>, Result<BigDecimal>> resultProducer,
        ParkingFeeCalculator next) {
        this.feeForRange = feeForRange;
        this.parkingHourRangeParkingHourBiFunction = parkingHourRangeParkingHourBiFunction;
        this.resultProducer = resultProducer;
        this.next = next;
        this.range = range;
    }

    @Override
    public Result<BigDecimal> calculate(ParkingHour parkingHour) {
        if (parkingHour.beyond(this.range)) {
            return resultProducer.apply(
                new Success<>(feeForRange),
                next.calculate(parkingHourRangeParkingHourBiFunction.apply(parkingHour, range)));
        }
        return parkingHour.between(this.range)
            ? new Success<>(feeForRange)
            : new InvalidRangeError<>();
    }

    public static HourlyRangeParkingFeeCalculator ofWithIdentical(Range range,
        BigDecimal feeForRange,
        ParkingFeeCalculator next) {
        checkNull(range, feeForRange, next);
        return new HourlyRangeParkingFeeCalculator(range,
            feeForRange,
            (parkingHour, r) -> parkingHour,
            (result1, result2) -> result2,
            next);
    }

    public static HourlyRangeParkingFeeCalculator ofWithSum(Range range,
        BigDecimal feeForRange,
        BiFunction<ParkingHour, Range, ParkingHour> parkingHourRangeParkingHourBiFunction,
        ParkingFeeCalculator next) {
        checkNull(range, feeForRange, next);
        return new HourlyRangeParkingFeeCalculator(range,
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
        ParkingFeeCalculator next) {
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
