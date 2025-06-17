package net.miarma.api.common;

public record LogEntry(int order, String message) {
    public static LogEntry of(int order, String message) {
        return new LogEntry(order, message);
    }
}
