package net.miarma.api.common.db;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import net.miarma.api.common.Constants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor centralizado de acceso a la base de datos utilizando Vert.x SQL Client.
 *
 * <p>
 * Esta clase sigue el patron Singleton para asegurar una sola instancia.
 *
 *
 * @author José Manuel Amador Gallardo
 */
public class DatabaseManager {

	private static DatabaseManager instance;
	private final Pool pool;

	/**
	 * Constructor privado para seguir el patrón Singleton.
	 *
	 * @param pool el pool de conexiones proporcionado por Vert.x
	 */
	private DatabaseManager(Pool pool) {
		this.pool = pool;
	}

	/**
	 * Devuelve la instancia única de {@link DatabaseManager}. Si no existe, la crea.
	 *
	 * @param pool el pool de conexiones a reutilizar
	 * @return la instancia singleton de DatabaseManager
	 */
	public static synchronized DatabaseManager getInstance(Pool pool) {
		if (instance == null) {
			instance = new DatabaseManager(pool);
		}
		return instance;
	}

	/**
	 * Devuelve el pool de conexiones actual.
	 *
	 * @return el pool de conexiones
	 */
	public Pool getPool() {
		return pool;
	}

	/**
	 * Realiza una consulta simple para verificar que la conexión con la base de datos funciona.
	 *
	 * @return un {@link Future} que representa el resultado de la consulta
	 */
	public Future<RowSet<Row>> testConnection() {
		return pool.query("SELECT 1").execute();
	}

	/**
	 * Ejecuta una consulta SQL que devuelve múltiples resultados y los convierte en objetos de tipo {@code T}.
	 *
	 * @param query     la consulta SQL a ejecutar
	 * @param clazz     clase del objeto a instanciar desde cada fila del resultado
	 * @param onSuccess callback que se ejecuta si la consulta fue exitosa
	 * @param onFailure callback que se ejecuta si ocurre un error
	 * @param <T>       tipo del objeto a devolver
	 * @return un {@link Future} con la lista de resultados convertidos
	 */
	public <T> Future<List<T>> execute(String query, Class<T> clazz, Handler<List<T>> onSuccess,
									   Handler<Throwable> onFailure) {
		return pool.query(query).execute().map(rows -> {
			List<T> results = new ArrayList<>();
			for (Row row : rows) {
				try {
					Constructor<T> constructor = clazz.getConstructor(Row.class);
					results.add(constructor.newInstance(row));
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
						 | InvocationTargetException e) {
					Constants.LOGGER.error("Error instantiating class: {}", e.getMessage());
				}
			}
			return results;
		}).onComplete(ar -> {
			if (ar.succeeded()) {
				onSuccess.handle(ar.result());
			} else {
				onFailure.handle(ar.cause());
			}
		});
	}

	/**
	 * Ejecuta una consulta SQL que devuelve como máximo una fila y la convierte en un objeto de tipo {@code T}.
	 *
	 * @param query     la consulta SQL a ejecutar
	 * @param clazz     clase del objeto a instanciar desde la fila del resultado
	 * @param onSuccess callback que se ejecuta si la consulta fue exitosa
	 * @param onFailure callback que se ejecuta si ocurre un error
	 * @param <T>       tipo del objeto a devolver
	 * @return un {@link Future} con el objeto instanciado, o null si no hay resultados
	 */
	public <T> Future<T> executeOne(String query, Class<T> clazz, Handler<T> onSuccess, Handler<Throwable> onFailure) {
		return pool.query(query).execute().map(rows -> {
			for (Row row : rows) {
				try {
					Constructor<T> constructor = clazz.getConstructor(Row.class);
					return constructor.newInstance(row);
				} catch (Exception e) {
                    Constants.LOGGER.error("Error instantiating class: {}", e.getMessage());
				}
			}
			return null; // Si no hay filas
		}).onComplete(ar -> {
			if (ar.succeeded()) {
				onSuccess.handle(ar.result());
			} else {
				onFailure.handle(ar.cause());
			}
		});
	}
}
