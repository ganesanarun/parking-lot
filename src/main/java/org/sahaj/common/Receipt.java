package org.sahaj.common;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record Receipt(ZonedDateTime start, ZonedDateTime end, BigDecimal parkingFee) {

}
