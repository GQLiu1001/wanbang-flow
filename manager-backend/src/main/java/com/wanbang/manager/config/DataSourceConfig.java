package com.wanbang.manager.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
@EnableConfigurationProperties
@Configuration
// @EnableConfigurationProperties // 如果主类上有，这里可以移除
public class DataSourceConfig {

    // 注入属性类
    private final PrimaryDataSourceProperties primaryProps;
    private final SecondaryDataSourceProperties secondaryProps;

    public DataSourceConfig(PrimaryDataSourceProperties primaryProps, SecondaryDataSourceProperties secondaryProps) {
        this.primaryProps = primaryProps;
        this.secondaryProps = secondaryProps;
    }

    @Bean(name = "primaryDataSource")
    @Primary
    // 移除 @ConfigurationProperties
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create()
                .url(primaryProps.getUrl())
                .username(primaryProps.getUsername())
                .password(primaryProps.getPassword())
                .driverClassName(primaryProps.getDriverClassName())
                .build();
    }

    @Bean(name = "secondaryDataSource")
    // 移除 @ConfigurationProperties
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create()
                .url(secondaryProps.getUrl())
                .username(secondaryProps.getUsername())
                .password(secondaryProps.getPassword())
                .driverClassName(secondaryProps.getDriverClassName())
                .build();
    }

    @Bean(name = "primarySqlSessionFactory")
    @Primary
    // 确认这里的注入名称和 SeataConfig 中的 Bean 名称一致
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSourceProxy") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        // 确保路径正确
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/db1/*.xml"));
        factoryBean.setPlugins(primaryMybatisPlusInterceptor());
        return factoryBean.getObject();
    }

    @Bean(name = "secondarySqlSessionFactory")
    // 确认这里的注入名称和 SeataConfig 中的 Bean 名称一致
    public SqlSessionFactory secondarySqlSessionFactory(@Qualifier("secondaryDataSourceProxy") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        // 确保路径正确
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/db2/*.xml"));
        factoryBean.setPlugins(secondaryMybatisPlusInterceptor());
        return factoryBean.getObject();
    }

    @Bean
    public MybatisPlusInterceptor primaryMybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 可添加其他插件（如乐观锁）
        // interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    @Bean
    public MybatisPlusInterceptor secondaryMybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 可添加其他插件（如乐观锁）
        // interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}

