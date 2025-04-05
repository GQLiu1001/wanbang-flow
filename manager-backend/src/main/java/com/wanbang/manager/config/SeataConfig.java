package com.wanbang.manager.config;

import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
public class SeataConfig {
    @Bean("primaryDataSourceProxy")
    public DataSourceProxy primaryDataSourceProxy(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean("secondaryDataSourceProxy")
    public DataSourceProxy secondaryDataSourceProxy(@Qualifier("secondaryDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }
}
