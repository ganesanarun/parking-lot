package org.sahaj.parking;

import org.sahaj.calculators.VoucherType;

import java.util.Map;
import java.util.Optional;

public class InMemoryVoucherRepository {

    private final Map<String, Voucher> vehicleVoucher;

    public InMemoryVoucherRepository(Map<String, Voucher> vehicleVoucher) {
        this.vehicleVoucher = vehicleVoucher;
    }

    public Optional<VoucherType> voucherFor(String vehicleNumber) {
        if (!vehicleVoucher.containsKey(vehicleNumber)) {
            return Optional.empty();
        }
        return Optional.of(vehicleVoucher.get(vehicleNumber).type());
    }
}
