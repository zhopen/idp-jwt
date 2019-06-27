package com.hpe.cloud.management.db;

import com.github.rkmk.container.FoldingList;
import com.hpe.cloud.management.core.LoginLog;
import com.hpe.cloud.management.util.LogSqlFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.Date;
import java.util.List;

@UseStringTemplate3StatementLocator
@LogSqlFactory
public interface LoginLogDao {

    @SqlUpdate("insert into t_login_log (user_id,action,message,create_time) values (:userId,:action,:message,now())")
    @GetGeneratedKeys
    int insert(@Bind("userId") int userId, @Bind("action") String action, @Bind("message") String message);

    @SqlQuery("select id,user_id as userId,action,message,create_time as createTime from t_login_log order by <orderBy>")
    @MapResultAsBean
    List<LoginLog> loginlist(@Define("orderBy") String orderBy);

    @SqlQuery("select l.id,l.user_id,l.action,l.message,l.create_time AS createTime,u.id as user$id,u.name as user$name," +
        "u.email as user$email,u.mobile as user$mobile,u.avatar as user$avatar,u.remark as user$remark " +
        "from t_user u,t_login_log l where u.id = l.user_id and (u.email like :query_key or u.name like :query_key) " +
        "and l.action like :action and l.create_time between :from and :to order by <order_by> <order> limit :start,:count")
    FoldingList<LoginLog> loginloglist(@Bind("query_key") String queryKey, @Bind("action") String action, @Bind("from") Date from, @Bind("to") Date to,
                                       @Define("order_by") String orderBy, @Define("order") String order, @Bind("start") int start, @Bind("count") int count);


    @SqlQuery("select count(1) AS count " +
        "from t_user u,t_login_log l where u.id = l.user_id and (u.email like :query_key or u.name like :query_key) " +
        "and l.action like :action and l.create_time between :from and :to order by <order_by> <order> ")
    @MapResultAsBean
    Integer logcount(@Bind("query_key") String queryKey, @Bind("action") String action, @Bind("from") Date from, @Bind("to") Date to,
                     @Define("order_by") String orderBy, @Define("order") String order);


}
