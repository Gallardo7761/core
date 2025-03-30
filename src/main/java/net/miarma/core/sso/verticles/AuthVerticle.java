package net.miarma.core.sso.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class AuthVerticle extends AbstractVerticle {

	private JWTAuth authProvider;
	
    @Override
    public void start(Promise<Void> startFuture) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        JWTAuthOptions config = new JWTAuthOptions()
		  .setKeyStore(new KeyStoreOptions()
		    .setPath("debug.keystore")
		    .setPassword("android"));

        authProvider = JWTAuth.create(vertx, config);

        // Rutas públicas
        router.post("/auth/login").handler(this::login);
        router.post("/auth/refresh").handler(this::refreshToken);
        router.get("/").handler(ctx -> ctx.response().end("Servidor corriendo"));
        
        
        // Rutas protegidas
        router.route("/protected/*").handler(JWTAuthHandler.create(authProvider));
        router.get("/protected/data").handler(this::protectedEndpoint);

        vertx.createHttpServer().requestHandler(router).listen(8888, res -> {
            if (res.succeeded()) {
                System.out.println("Servidor corriendo en el puerto 8888");
                startFuture.complete();
            } else {
                startFuture.fail(res.cause());
            }
        });
    }

    /**
     * Endpoint de login: devuelve access y refresh tokens
     */
    private void login(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String userName = body.getString("userName");
        String password = body.getString("password");

        // Aquí deberías comprobar las credenciales en la base de datos
        if ("Gallardo7761".equals(userName) && "mamamama".equals(password)) {

            String userId = "438f7a02-0632-11f0-8d70-d30fd0e715af"; // Simulación de ID de usuario

            // Generar Access Token (válido por 15 minutos)
            String accessToken = this.authProvider.generateToken(
                new JsonObject()
                    .put("sub", userId)
                    .put("userName", userName)
                    .put("role", "user"),
                new JWTOptions().setExpiresInMinutes(15)
            );

            // Generar Refresh Token (válido por 7 días)
            String refreshToken = this.authProvider.generateToken(
                new JsonObject()
                    .put("sub", userId)
                    .put("type", "refresh"),
                new JWTOptions().setExpiresInMinutes(10080) // 7 días
            );

            ctx.json(new JsonObject().put("accessToken", accessToken).put("refreshToken", refreshToken));
        } else {
            ctx.response().setStatusCode(401).end("Credenciales incorrectas");
        }
    }

    /**
     * Endpoint de refresh: genera un nuevo access token usando un refresh token válido
     */
    @SuppressWarnings("unused")
	private void refreshToken(RoutingContext ctx) {
        String refreshToken = ctx.body().asJsonObject().getString("refreshToken");

        this.authProvider.authenticate(new io.vertx.ext.auth.authentication.TokenCredentials(refreshToken))
            .onSuccess(user -> {
                String userId = user.principal().getString("sub");

                // Generar nuevo Access Token
                String newAccessToken = this.authProvider.generateToken(
                    new JsonObject()
                        .put("sub", userId)
                        .put("role", "user"),
                    new JWTOptions().setExpiresInMinutes(15)
                );

                ctx.json(new JsonObject().put("accessToken", newAccessToken));
            })
            .onFailure(_err -> ctx.response().setStatusCode(401).end("Refresh Token inválido"));
    }

    /**
     * Endpoint protegido: Solo accesible con un Access Token válido
     */
    private void protectedEndpoint(RoutingContext ctx) {
        String userId = ctx.user().principal().getString("sub");
        ctx.json(new JsonObject()
            .put("message", "Bienvenido al endpoint protegido")
            .put("userId", userId));
    }
}
