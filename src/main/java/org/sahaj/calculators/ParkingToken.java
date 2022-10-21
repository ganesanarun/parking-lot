package org.sahaj.calculators;

import org.sahaj.Size;

import java.time.ZonedDateTime;

public record ParkingToken(ZonedDateTime entryTime, ZonedDateTime exitTime, Size size) {

}
