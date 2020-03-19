package com.foo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfiguration {

    @Bean("trigDatasource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource trigDatasource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        return dataSource;
    }

}
