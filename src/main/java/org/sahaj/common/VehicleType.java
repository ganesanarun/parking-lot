package org.sahaj.common;

public sealed interface VehicleType {

    record Bike() implements VehicleType {

    }

    record Car() implements VehicleType {

    }

    record Truck() implements VehicleType {

    }
}
