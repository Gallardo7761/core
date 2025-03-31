package net.miarma.core.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // aplica a clases/interfaces
@Retention(RetentionPolicy.RUNTIME) // disponible en tiempo de ejecución
public @interface APIDontReturn {}
