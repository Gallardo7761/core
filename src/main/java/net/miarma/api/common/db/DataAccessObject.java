package net.miarma.api.common.db;

import io.vertx.core.Future;

import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD básicas en una base de datos,
 * adaptada al modelo asincrónico de Vert.x usando {@link Future}.
 *
 * @param <T>  Tipo de la entidad gestionada.
 * @param <ID> Tipo del identificador único de la entidad.
 *
 * @author José Manuel Amador Gallardo
 */
public interface DataAccessObject<T, ID> {

	/**
	 * Recupera todos los registros de la entidad.
	 *
	 * @return Un {@link Future} que contiene una lista con todas las entidades encontradas.
	 */
	Future<List<T>> getAll();

	/**
	 * Recupera una entidad por su identificador.
	 *
	 * @param id Identificador de la entidad.
	 * @return Un {@link Future} que contiene la entidad, o falla si no se encuentra.
	 */
	Future<T> getById(ID id);

	/**
	 * Inserta una nueva entidad en la base de datos.
	 *
	 * @param t Entidad a insertar.
	 * @return Un {@link Future} que contiene la entidad insertada, posiblemente con su ID asignado.
	 */
	Future<T> insert(T t);

	/**
	 * Inserta o actualiza una entidad en la base de datos.
	 * Si la entidad ya existe, se actualiza; si no, se inserta como nueva.
	 *
	 * @param t Entidad a insertar o actualizar.
	 * @return Un {@link Future} que contiene la entidad insertada o actualizada.
	 */
	Future<T> upsert(T t, String... conflictKeys);

	/**
	 * Actualiza una entidad existente.
	 *
	 * @param t Entidad con los datos actualizados.
	 * @return Un {@link Future} que contiene la entidad actualizada.
	 */
	Future<T> update(T t);

	/**
	 * Elimina una entidad por su identificador.
	 *
	 * @param id Identificador de la entidad a eliminar.
	 * @return Un {@link Future} que indica si la operación fue exitosa.
	 */
	Future<Boolean> delete(ID id);

	/**
	 * Comprueba si existe una entidad con el identificador proporcionado.
	 *
	 * @param id Identificador a comprobar.
	 * @return Un {@link Future} que contiene {@code true} si existe, o {@code false} si no.
	 */
	Future<Boolean> exists(ID id);
}
