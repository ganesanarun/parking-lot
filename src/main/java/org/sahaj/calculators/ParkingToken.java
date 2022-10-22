package org.sahaj.calculators;

import org.sahaj.common.Size;

import java.time.ZonedDateTime;

public record ParkingToken(ZonedDateTime entryTime, ZonedDateTime exitTime, Size size) {

}
