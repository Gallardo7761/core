package net.miarma.api.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotación se utiliza para indicar que un campo no debe ser incluido en la respuesta de la API.
 * Se aplica a campos de clases o interfaces y está disponible en tiempo de ejecución.
 *
 * @author José Manuel Amador Gallardo
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface APIDontReturn {}
