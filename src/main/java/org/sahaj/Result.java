package org.sahaj;


public sealed interface Result<T> {

    record Success<T>(T value) implements Result<T> {

    }

    record InvalidRangeError<T>() implements Result<T> {

    }
}
