package com.hpe.cloud.management.resources;

import com.hpe.cloud.management.core.LoginLog;
import com.hpe.cloud.management.db.LoginLogDao;
import com.hpe.cloud.management.vo.PageList;
import com.hpe.cloud.management.vo.PageParam;
import com.hpe.cloud.management.vo.ServerResponse;
import io.dropwizard.jersey.params.IntParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Path("/v1/log")
@Produces(MediaType.APPLICATION_JSON)
public class LoginLogResource {

    private LoginLogDao loginLogDao;

    private Logger logger = LoggerFactory.getLogger(LoginLogResource.class);

    public LoginLogResource(LoginLogDao loginLogDao) {
        this.loginLogDao = loginLogDao;
    }


    @POST
    @Path("/add")
    public String addLogs(LoginLog log) {
        System.out.println(log.toString());
        int insert = loginLogDao.insert(log.getUserId(), log.getAction(), log.getMessage());
        System.out.println("insert:" + insert);
        List<LoginLog> list = loginLogDao.loginlist("create_time");
        System.out.println(list.size());
        return "success";
    }

    /**
     * 获取日志列表
     */
    @GET
    @Path("/logs")
    public ServerResponse logsList(@QueryParam("page_size") @DefaultValue("10") IntParam pageSize,
                                   @QueryParam("page_num") @DefaultValue("1") IntParam pageNum,
                                   @QueryParam("order_by") @DefaultValue("create_time") String orderBy,
                                   @QueryParam("order") @DefaultValue("desc") String order,
                                   @QueryParam("start_time") @DefaultValue("") String startTime,
                                   @QueryParam("end_time") @DefaultValue("") String endTime,
                                   @QueryParam("user") @DefaultValue("") String queryKey,
                                   @QueryParam("action") @DefaultValue("") String action) {
        PageList<LoginLog> pageList = null;
        try {
            PageParam param = new PageParam(pageNum.get(), pageSize.get(), orderBy, order, startTime, endTime, queryKey, action, null, null);
            param = verifParam(param);
            System.out.println("参数：" + param.toString());

            int count = loginLogDao.logcount(param.getQueryKey(), param.getAction(), param.getFromDate(), param.getToDate(), param.getOrderBy(), param.getOrder());
            List<LoginLog> loginloglist = loginLogDao.loginloglist(param.getQueryKey(), param.getAction(), param.getFromDate(), param.getToDate(), param.getOrderBy(),
                param.getOrder(), (param.getPageNum() - 1) * param.getPageSize(), param.getPageSize()).getValues();

            pageList = new PageList<>(count, loginloglist);
        } catch (Exception e) {
            logger.error("logs error：" + e.getMessage());
            return new ServerResponse(500, e.getMessage());
        }
        return new ServerResponse(200, "success", pageList);
    }

    /**
     * 前端参数校验
     *
     * @param param
     * @return
     */
    public PageParam verifParam(PageParam param) {
        Date fromDate = null;
        Date toDate = null;
        TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai"); //获取中国的时区
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        sdf.setTimeZone(timeZoneChina);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        try {
            param.setPageNum(param.getPageNum() > 0 ? param.getPageNum() : 1);
            param.setPageSize(param.getPageSize() > 0 ? param.getPageSize() : 10);
            param.setQueryKey(StringUtils.isBlank(param.getQueryKey()) ? "%%" : "%" + param.getQueryKey() + "%");
            param.setAction(StringUtils.isBlank(param.getAction()) ? "%%" : "%" + param.getAction() + "%");
            param.setOrderBy("l." + (StringUtils.isBlank(param.getOrderBy()) ? "create_time" : param.getOrderBy()));
            param.setOrder("asc".equals(param.getOrder()) ? "asc" : "desc");
            param.setFromDate(StringUtils.isBlank(param.getStartTime()) ? cal.getTime() : sdf.parse(param.getStartTime()));
            param.setToDate(StringUtils.isBlank(param.getEndTime()) ? new Date() : sdf.parse(param.getEndTime()));
        } catch (ParseException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return param;
    }

}
