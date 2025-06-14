package net.miarma.api.common.db;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import net.miarma.api.common.ConfigManager;

/**
 * Factoría de {@link Pool} para conexiones MySQL usando Vert.x.
 *
 * <p>
 * Se apoya en {@link ConfigManager} para extraer la configuración de la BBDD
 * (host, puerto, nombre, usuario y contraseña) y crea un pool con un tamaño
 * máximo de 10 conexiones.
 * </p>
 *
 * @author José Manuel Amador Gallardo
 */
public class DatabaseProvider {

    /**
     * Crea y configura un pool de conexiones MySQL.
     *
     * @param vertx  instancia principal de Vert.x
     * @param config gestor de configuración con las propiedades necesarias:
     *               <ul>
     *                   <li><b>db.port</b> – puerto del servidor MySQL</li>
     *                   <li><b>db.host</b> – host o IP</li>
     *                   <li><b>db.name</b> – nombre de la base de datos</li>
     *                   <li><b>db.user</b> – usuario de la base</li>
     *                   <li><b>db.password</b> – contraseña del usuario</li>
     *               </ul>
     * @return un {@link Pool} listo para usarse en consultas Vert.x
     */
    public static Pool createPool(Vertx vertx, ConfigManager config) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(config.getIntProperty("db.port"))
                .setHost(config.getStringProperty("db.host"))
                .setDatabase(config.getStringProperty("db.name"))
                .setUser(config.getStringProperty("db.user"))
                .setPassword(config.getStringProperty("db.password"));

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(10);

        return Pool.pool(vertx, connectOptions, poolOptions);
    }
}
