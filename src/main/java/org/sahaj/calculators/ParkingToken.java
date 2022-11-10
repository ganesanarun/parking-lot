package org.sahaj.calculators;

import org.sahaj.common.Size;

import java.time.ZonedDateTime;
import java.util.Optional;

public record ParkingToken(ZonedDateTime entryTime, ZonedDateTime exitTime, Size size, VoucherType voucherType) {

    public ParkingToken(ZonedDateTime entryTime, ZonedDateTime exitTime, Size size)
    {
        this(entryTime, exitTime, size, null);
    }

    public Optional<VoucherType> getVoucherType() {
        return Optional.of(this.voucherType);
    }
}
