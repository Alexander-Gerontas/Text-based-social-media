package com.alg.social_media.configuration.security;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import javax.inject.Inject;

public class SecurityMiddleware {
  private final Javalin app;

  @Inject
  public SecurityMiddleware(Javalin app) {
    this.app = app;
    configureRoutes();
  }

  private void configureRoutes() {
    // fixme add actual secure endpoints
    app.before("/api/v1/secure/*", authHandler);
  }

  private final Handler authHandler = context -> {
    String token = context.header("Authorization");

    if (token == null || !token.startsWith("Bearer ")) {
      context.status(401).json("Unauthorized");
      return;
    }

    try {
      String username = JwtUtil.extractUsername(token.substring(7));
      // Perform authorization logic here if needed

      // Continue to the next handler
      context.attribute("username", username);
    } catch (Exception e) {
      context.status(401).json("Unauthorized");
    }
  };
}
