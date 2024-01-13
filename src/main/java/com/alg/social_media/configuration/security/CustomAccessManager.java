package com.alg.social_media.configuration.security;

import static com.alg.social_media.constants.Keywords.ROLE;

import com.alg.social_media.enums.AccountType;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.Header;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.AccessManager;
import io.javalin.security.RouteRole;
import java.util.Set;

public class CustomAccessManager implements AccessManager {
  @Override
  public void manage(Handler handler, Context ctx, Set<? extends RouteRole> permittedRoles)
      throws Exception {

    // Check if the user has the required roles to access the route
    if (permittedRoles.contains(AccountType.ANYONE) || permittedRoles.contains(ctx.attribute(ROLE))) {
        handler.handle(ctx);
        return;
    }

    ctx.header(Header.WWW_AUTHENTICATE, "Basic");
    throw new UnauthorizedResponse("Unauthorized");
  }
}
