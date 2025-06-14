package net.miarma.api.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Clase de utilidad para convertir fechas a segundos desde la época Unix.
 * @author José Manuel Amador Gallardo
 */
public class DateParser {
	public static long parseDate(LocalDateTime date) {
		return date.toEpochSecond(ZoneOffset.UTC);
	}
}
