package net.miarma.api.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para definir el nombre de una tabla en la base de datos.
 * Se utiliza para mapear una clase a una tabla específica.
 *
 * @author José Manuel Amador Gallardo
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	String value();
}
