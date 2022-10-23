package org.sahaj.parking;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class InMemoryTicketNumberGenerator implements TicketNumberGenerator {

    private final AtomicInteger atomicInteger;
    private final Supplier<String> prefixProducer;

    public InMemoryTicketNumberGenerator(int initialValue, Supplier<String> prefixProducer) {
        atomicInteger = new AtomicInteger(initialValue);
        this.prefixProducer = prefixProducer;
    }

    @Override
    public String nextOne() {
        return prefixProducer.get() + String.format("%03d", atomicInteger.getAndIncrement());
    }
}
