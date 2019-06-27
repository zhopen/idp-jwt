package com.hpe.cloud.management.resources;

import com.codahale.metrics.annotation.Timed;
import com.hpe.cloud.management.core.User;
import com.hpe.cloud.management.db.UserDao;
import com.hpe.cloud.management.jwt.IdpJwt;
import com.hpe.cloud.management.util.LogSqlFactory;
import com.hpe.cloud.management.vo.PageList;
import io.dropwizard.jersey.params.IntParam;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

//TODO without token check, exception check
@LogSqlFactory
@Path("/v1/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserDao userDao;
    private final IdpJwt jwt;

    public UserResource(UserDao userDao, IdpJwt jwt) {
        this.userDao = userDao;
        this.jwt = jwt;
    }

    @POST
    @Path("/users")
    @Timed
    public User createUser(User user) {
        return userDao.insert(user);
    }

    @GET
    @Path("/touch")
    @Timed
    public User getCurrentUser(@CookieParam("token") String token) {
        String id = jwt.getValueFromToken(token, "username");
        return userDao.findUser(Integer.parseInt(id));
    }

    @DELETE
    @Timed
    @Path("/users/{user_id}")
    public String deleteUser(@PathParam("user_id") int id) {
        userDao.delete(id);
        return "{}";
    }

    @PUT
    @Timed
    @Path("/users/{user_id}")
    public User updateUser(@PathParam("user_id") int userId, User user) {
        user.setId(userId);
        return userDao.update(user);
    }

    @GET
    @Path("/users")
    @Timed
    public PageList listUser(@QueryParam("page_size") @DefaultValue("10") IntParam pageSize,
                             @QueryParam("page_num") @DefaultValue("1") IntParam pageNum,
                             @QueryParam("query_key") @DefaultValue("") String queryKey,
                             @QueryParam("order_by") @DefaultValue("create_time") String orderBy,
                             @QueryParam("order") @DefaultValue("desc") String order) {
        return userDao.list(pageSize.get(), pageNum.get(), queryKey, orderBy, order);
    }

    @GET
    @Timed
    @Path("/users/{user_id}")
    public User getUser(@PathParam("user_id") int id) {
        return userDao.findUser(id);
    }

    @PUT
    @Timed
    @Path("/users/{userId}/password")
    public String updateUserPassword(@PathParam("userId") int id, User user) {
        userDao.updateUserPassword(user.getPassword(), id);
        return "{}";
    }

}
