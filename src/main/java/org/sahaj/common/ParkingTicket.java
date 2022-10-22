package org.sahaj.common;

public record ParkingTicket(String spotNumber,
                            java.time.ZonedDateTime entryTime,
                            String ticketNumber,
                            Size size) {

}
