package com.hpe.cloud.management;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class CloudIdpConfiguration extends Configuration {

    JWT jwt;
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    CloudIdpConfiguration() {

    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("jwt")
    public JWT getJwt() {
        return jwt;
    }

    @JsonProperty("jwt")
    public void setJwt(JWT jwt) {
        this.jwt = jwt;
    }

    public class JWT {
        private long expiresIn = 0;
        private long refreshExpiresIn = 0;
        private String hmacSecret = "123456";

        @JsonProperty("expiresIn")
        public long getExpiresIn() {
            return expiresIn;
        }

        @JsonProperty("expiresIn")
        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            System.out.print(expiresIn);
        }

        @JsonProperty("refreshExpiresIn")
        public long getRefreshExpiresIn() {
            return refreshExpiresIn;
        }

        @JsonProperty("refreshExpiresIn")
        public void setRefreshExpiresIn(long expiresIn) {
            this.refreshExpiresIn = expiresIn;
        }

        @JsonProperty("hmacSecret")
        public String getHmacSecret() {
            return hmacSecret;
        }

        @JsonProperty("hmacSecret")
        public void setHmacSecret(String hmacSecret) {
            this.hmacSecret = hmacSecret;
        }
    }

}
