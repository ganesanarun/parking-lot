package org.sahaj.common;

public sealed interface ParkingResult<T> {

    record Success<T>(T value) implements ParkingResult<T> {

    }

    record NoOpenParkingSpot<T>() implements ParkingResult<T> {

    }

    record UnsupportedVehicle<T>() implements ParkingResult<T> {

    }

}
