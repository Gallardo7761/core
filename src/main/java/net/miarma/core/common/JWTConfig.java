package net.miarma.core.common;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.github.cdimascio.dotenv.Dotenv;

public class JWTConfig {
    public static JWTAuth setupJWTAuth(Vertx vertx) {
        Dotenv dotenv = Dotenv.load();
        String secret = dotenv.get("JWT_SECRET");

        return JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setSecretKey(secret)
            )
        );
    }
}
