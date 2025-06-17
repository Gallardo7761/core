package net.miarma.api.common;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LogAccumulator {
    private static final List<LogEntry> LOGS = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static void add(String message) {
        LOGS.add(LogEntry.of(COUNTER.getAndIncrement(), message));
    }

    public static void flushToLogger(Logger logger) {
        LOGS.stream()
                .sorted(Comparator.comparingInt(LogEntry::order))
                .forEach(entry -> logger.info(entry.message()));
        LOGS.clear();
    }
}
