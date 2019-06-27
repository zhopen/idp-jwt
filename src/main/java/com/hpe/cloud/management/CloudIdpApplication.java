package com.hpe.cloud.management;

import com.github.rkmk.container.FoldingListContainerFactory;
import com.github.rkmk.mapper.CustomMapperFactory;
import com.hpe.cloud.management.db.LoginLogDao;
import com.hpe.cloud.management.db.UserDao;
import com.hpe.cloud.management.jwt.IdpJwt;
import com.hpe.cloud.management.resources.IdpWebApplicationExceptionMapper;
import com.hpe.cloud.management.resources.LoginLogResource;
import com.hpe.cloud.management.resources.TokenResource;
import com.hpe.cloud.management.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class CloudIdpApplication extends Application<CloudIdpConfiguration> {

    public static void main(final String[] args) throws Exception {
        new CloudIdpApplication().run(args);
    }

    @Override
    public String getName() {
        return "cloud.managementIdp";
    }

    @Override
    public void initialize(final Bootstrap<CloudIdpConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<CloudIdpConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(CloudIdpConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(final CloudIdpConfiguration configuration, final Environment environment) {
        final DBIFactory fac = new DBIFactory();
        final DBI dbi = fac.build(environment, configuration.getDataSourceFactory(), "postgresql");
        final UserDao dao = dbi.onDemand(UserDao.class);
        final LoginLogDao loginLogDao = dbi.onDemand(LoginLogDao.class);

        IdpJwt jwt = IdpJwt.getInstance(
            configuration.getJwt().getExpiresIn(),
            configuration.getJwt().getRefreshExpiresIn(),
            configuration.getJwt().getHmacSecret(),
            "hpe-idp"
        );

        dbi.registerMapper(new CustomMapperFactory());
        dbi.registerContainerFactory(new FoldingListContainerFactory());

        environment.jersey().register(new IdpWebApplicationExceptionMapper());

        environment.jersey().register(new UserResource(dao, jwt));
        environment.jersey().register(new TokenResource(dao, loginLogDao, jwt));
        environment.jersey().register(new LoginLogResource(loginLogDao));
    }

}
