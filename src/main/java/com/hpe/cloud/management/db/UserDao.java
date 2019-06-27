package com.hpe.cloud.management.db;

import com.hpe.cloud.management.core.User;
import com.hpe.cloud.management.util.LogSqlFactory;
import com.hpe.cloud.management.vo.PageList;
import com.mysql.jdbc.StringUtils;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

@UseStringTemplate3StatementLocator
@LogSqlFactory
public abstract class UserDao {

    @Transaction
    public User insert(User user) {
        //TODO Can insert return all other data?
        //TODO if there is an error in find, the id can't be reverted.
        int id = insertUser(user.getEmail(), user.getRoles(), user.getName(), user.getMobile(), user.getAvatar(),
            user.getPassword(), user.getRemark());
        return findUser(id);
    }

    @Transaction
    public void delete(int id) {
        deleteUser(id);
    }

    @Transaction
    public User update(User user) {
        updateUser(user.getEmail(), user.getRoles(), user.getName(), user.getMobile(), user.getAvatar(), user.getRemark(), user.getId());
        return findUser(user.getId());
    }

    /**
     * @param pageSize data count for one page, default 10
     * @param pageNum  begin with 1
     * @param queryKey key word for email or name, default empty
     * @param orderBy  default column is create_time
     * @param order    default desc
     */
    public PageList<User> list(int pageSize, int pageNum, String queryKey, String orderBy, String order) {
        pageNum = pageNum > 0 ? pageNum : 1; //TODO default page begins at 1
        pageSize = pageSize > 0 ? pageSize : 10;
        queryKey = queryKey == null ? "" : queryKey;
        orderBy = StringUtils.isEmptyOrWhitespaceOnly(orderBy) ? "create_time" : orderBy;
        order = order.equals("asc") ? "asc" : "desc";
        List<User> users = findUsers(queryKey, orderBy, order, (pageNum - 1) * pageSize, pageSize);
        int count = countUsers(queryKey);
        return new PageList(count, users);
    }

    @SqlUpdate("insert into t_user (email, roles, name, mobile, avatar, password, remark, enable, create_time)" +
        " values (:email, :roles, :name, :mobile, :avatar, :password, :remark, true, now())")
    @GetGeneratedKeys
    abstract int insertUser(@Bind("email") String email, @Bind("roles") String roles, @Bind("name") String name,
                            @Bind("mobile") String mobile, @Bind("avatar") String avatar, @Bind("password") String password,
                            @Bind("remark") String remark);

    //@SqlUpdate("update t_user set enable = false, update_time = now() where id=:id")
    @SqlUpdate("delete from t_user where id=:id")
    abstract void deleteUser(@Bind("id") int id);

    @SqlUpdate("update t_user set email = :email, roles = :roles, name = :name, " +
        "mobile = :mobile, avatar = :avatar, remark = :remark, update_time = now() where id = :id")
    @GetGeneratedKeys
    abstract int updateUser(@Bind("email") String email, @Bind("roles") String roles, @Bind("name") String name,
                            @Bind("mobile") String mobile, @Bind("avatar") String avatar,
                            @Bind("remark") String remark, @Bind("id") int id);

    @SqlUpdate("update t_user set password = :new_password, update_time = now() " +
        "where id = :id")
    public abstract void updateUserPassword(@Bind("new_password") String newPassword, @Bind("id") int id);

    @SqlQuery("select id, email, roles, name, mobile, avatar, remark, enable, create_time, update_time " +
        "from t_user where t_user.id = :id ")
    @MapResultAsBean
    //TODO Optional<User>?
    public abstract User findUser(@Bind("id") int id);

    @SqlQuery("select id, email, roles, name, mobile, avatar, remark, enable, create_time " +
        "from t_user where email like \"%\":query_key\"%\" or name like \"%\":query_key\"%\" " +
        "order by <order_by> <order> limit :start,:count")
    @MapResultAsBean
    abstract List<User> findUsers(@Bind("query_key") String queryKey, @Define("order_by") String orderBy,
                                  @Define("order") String order, @Bind("start") int start, @Bind("count") int count);

    @SqlQuery("select count(*) " +
        "from t_user where email like \"%\":query_key\"%\" or name like \"%\":query_key\"%\" ")
    @MapResultAsBean
    abstract Integer countUsers(@Bind("query_key") String queryKey);

    @SqlQuery("select count(*) " +
        "FROM idp.t_user where email=:email and password=:password")
    @MapResultAsBean
    public abstract Integer validateUser(@Bind("email") String email, @Bind("password") String password);

    @SqlQuery("select id " +
        "FROM idp.t_user where email=:email ")
    @MapResultAsBean
    public abstract Integer getUserId(@Bind("email") String email);
}

