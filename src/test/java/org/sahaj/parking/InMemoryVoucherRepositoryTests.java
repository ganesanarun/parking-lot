package org.sahaj.parking;

import org.junit.jupiter.api.Test;
import org.sahaj.calculators.VoucherType;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sahaj.calculators.VoucherType.EMPLOYEE;

class InMemoryVoucherRepositoryTests {

    @Test
    void returnVoucherTypeIfItPresentForVehicle() {
        var vehicleNumber = "001";
        var vehicleVoucher = Map.of(vehicleNumber, new Voucher("randomNumber", EMPLOYEE));
        final var inMemoryVoucherRepository = new InMemoryVoucherRepository(vehicleVoucher);

        Optional<VoucherType> voucher = inMemoryVoucherRepository.voucherFor(vehicleNumber);

        assertTrue(voucher.isPresent());
        assertEquals(EMPLOYEE, voucher.get());
    }

    @Test
    void returnEmptyVoucherTypeIfVehicleDoesNotHaveVoucher() {
        final var inMemoryVoucherRepository = new InMemoryVoucherRepository(Map.of());

        Optional<VoucherType> voucher = inMemoryVoucherRepository.voucherFor("anyNumber");

        assertTrue(voucher.isEmpty());
    }
}
