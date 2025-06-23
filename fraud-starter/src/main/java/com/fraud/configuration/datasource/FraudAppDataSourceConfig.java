package com.fraud.configuration.datasource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

/**
 * @author zhouyang
 */
@Configuration
@MapperScan(basePackages = {"com.fraud.repository.app.mapper"},
        sqlSessionFactoryRef = "fraudSqlSessionFactory")
public class FraudAppDataSourceConfig {

    /**
     * 指定mapper xml文件路径
     */
    public static final String MAPPER_LOCATION = "classpath*:mapper/app/**/**/*Mapper*.xml";

    /**
     * 数据库参数配置
     * @return
     */

    /**
     * 数据源配置
     * @return
     */
    @Bean("fraudDataSource")
    @Qualifier(value = "fraudDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.app")
    public HikariDataSource primaryDataSource() {
        return new HikariDataSource();
    }

    /**
     * 事务管理器
     * @param dataSource
     * @return
     */
    @Bean(name = "fraudTransactionManager")
    public TransactionManager TransactionManager(@Qualifier("fraudDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * sqlsession factory
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "fraudSqlSessionFactory")
    public SqlSessionFactory gysSqlSessionFactory(@Qualifier("fraudDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(FraudAppDataSourceConfig.MAPPER_LOCATION));
        return sessionFactoryBean.getObject();
    }

    /**
     * jdbctemplates
     * @param dataSource
     * @return
     */
    @Bean(name = "fraudjdbcTemplate")
    public JdbcTemplate gysJdbcTemplate(@Qualifier("fraudDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
