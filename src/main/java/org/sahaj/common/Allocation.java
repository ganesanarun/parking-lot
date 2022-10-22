package org.sahaj.common;

import java.time.ZonedDateTime;

public record Allocation(String number,
                         ParkingSpot parkingSpot,
                         ZonedDateTime entryTime, Vehicle vehicle) {

}
