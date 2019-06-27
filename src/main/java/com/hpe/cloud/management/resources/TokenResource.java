package com.hpe.cloud.management.resources;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hpe.cloud.management.core.User;
import com.hpe.cloud.management.db.LoginLogDao;
import com.hpe.cloud.management.db.UserDao;
import com.hpe.cloud.management.jwt.IdpJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/v1")
public class TokenResource {

    Logger log = LoggerFactory.getLogger("");
    UserDao userDao;
    LoginLogDao loginLogDaogDao;
    IdpJwt jwt;

    public TokenResource(UserDao userDao, LoginLogDao loginLogDaogDao, IdpJwt jwt) {
        this.loginLogDaogDao = loginLogDaogDao;
        this.userDao = userDao;
        this.jwt = jwt;
    }

    @POST
    @Timed
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Login(LoginParam loginParam) {

        if (loginParam.username == null || loginParam.username.isEmpty() || loginParam.password == null || loginParam.password.isEmpty()) {
            throw new WebApplicationException("Param(username or password) error", Status.BAD_REQUEST);
        }

        if (!validateUser(loginParam.username, loginParam.password)) {
            throw new WebApplicationException("Username or password is invalid", Status.UNAUTHORIZED);
        }
        int userId = userDao.getUserId(loginParam.username);
        String token = jwt.generateToken(
            String.valueOf(userId), null, null);

        loginLogDaogDao.insert(userId, "login", "login system");

        return Response.ok()
            .cookie(new NewCookie("token", token, "/", null, null, -1, false))
            .entity(getUserDetail(loginParam.username))
            .build();
    }

    @POST
    @Timed
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@CookieParam("token") String token) {

        try {
            jwt.verifier.verify(token);
            String username = jwt.getValueFromToken(token, "username");
            loginLogDaogDao.insert(Integer.parseInt(username), "logout", "logout system");
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage(), Status.UNAUTHORIZED);
        }


        return Response.ok("{}").build();
    }

    @GET
    @Timed
    @Path("/test")
    public Response test() {
        return Response.ok("{}").cookie(new NewCookie("key1", "value1")).build();
    }


    @POST
    @Timed
    @Path("/token/refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(TokenParam token) {
        if (token == null) {
            throw new WebApplicationException("token param error!", Status.BAD_REQUEST);
        }

        try {
            String tokenStr = token.getToken();
            jwt.verifier.verify(tokenStr);
            if (!jwt.getValueFromToken(tokenStr, "type").equals("refresh_token")) {
                throw new WebApplicationException("token type error! type:refresh_token required:" + jwt.getValueFromToken(tokenStr, "type"), Status.BAD_REQUEST);
            }
        } catch (JWTVerificationException exception) {
            throw new WebApplicationException(exception.getMessage(), Status.BAD_REQUEST);
        } catch (Exception exception) {
            throw new WebApplicationException(exception.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
        String username = jwt.getValueFromToken(token.getToken(), "username");
        String responseBody;
        try {
            responseBody = jwt.generateTokenJson(
                username,
                getScope(username),
                token.getToken()
            );

        } catch (JsonProcessingException e) {
            throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }


        return Response.ok(responseBody).build();
    }

    @POST
    @Timed
    @Path("/token/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateToken(TokenParam token) {
        if (token == null) {
            throw new WebApplicationException("token param error!", Status.BAD_REQUEST);
        }

        try {
            DecodedJWT decodedJWT = jwt.verifier.verify(token.getToken());
        } catch (JWTVerificationException exception) {
            throw new WebApplicationException(exception.getMessage(), Status.BAD_REQUEST);
        } catch (Exception exception) {
            throw new WebApplicationException(exception.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }

        return Response.noContent().build();
    }


    boolean validateUser(String username, String password) {
        if (userDao.validateUser(username, password) == 0)
            return false;
        return true;
    }

    String getScope(String username) {
        String roles = "[\"TEST\", \"PUB\"]";
        //TODO: Please add real code
        //...
        return roles;
    }

    User getUserDetail(String username) {
        //TODO:
        Integer id = userDao.getUserId(username);
        User user = userDao.findUser(id);
        return user;
    }

//    void registerLogin(String info) {
//        //TODO:
//        loginLogDaogDao.in
//    }
}

class TokenParam {
    String token;

    public TokenParam() {
        this.token = "";
    }

    public TokenParam(String token) {
        this.token = token;
    }

    @JsonProperty("token")
    public String getToken() {
        return token;
    }

    @JsonProperty("token")
    public void setToken(String token) {
        this.token = token;
    }

}

class LoginParam {
    @JsonProperty("email")
    String username;
    String password;

    public LoginParam(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginParam() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

