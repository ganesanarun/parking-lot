package org.sahaj;


public sealed interface FeeResult<T> {

    record Success<T>(T value) implements FeeResult<T> {

    }

    record InvalidRangeError<T>() implements FeeResult<T> {

    }

    record SpotSizeNotConfiguredError<T>(Size missed) implements FeeResult<T> {

    }
}
