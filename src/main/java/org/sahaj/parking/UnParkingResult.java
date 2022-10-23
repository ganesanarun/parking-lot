package org.sahaj.parking;

public sealed interface UnParkingResult<T> {

    record Success<T>(T value) implements UnParkingResult<T> {

    }

    record EmptyParkingSpot<T>() implements UnParkingResult<T> {

    }

    record FailedConfigurationError<T>() implements UnParkingResult<T> {

    }

}
