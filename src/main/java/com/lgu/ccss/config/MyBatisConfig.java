package com.lgu.ccss.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public abstract class MyBatisConfig {
	 
	@Value("#{datasource['jdbc.use.type']}")
	private Integer dbType;

	@Value("#{datasource['jdbc.use2.type']}")
	private Integer dbType2;

	//public static final String BASE_PACKAGE = "com.lgu.ccss.mapper";
	public static final String BASE_PACKAGE = "com.lgu.ccss";
	public static final String TYPE_ALIASES_PACKAGE = "com.lgu.ccss";
	public static final String CONFIG_LOCATION_PATH = "classpath:META-INF/mybatis/sqlMapConfig.xml";

	// public static final String MAPPER_LOCATIONS_PATH ="classpath:META-INF/sqlmaps/*.xml";

	protected void configureSqlSessionFactoryMaster(
		SqlSessionFactoryBean sessionFactoryBean, DataSource dataSource) throws IOException {
		PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
		sessionFactoryBean.setConfigLocation(pathResolver.getResource(CONFIG_LOCATION_PATH));

		String MAPPER_LOCATIONS_PATH = "classpath:META-INF/sqlmaps/*/*.xml";
		
		
		/*if (dbType == 1) {
			MAPPER_LOCATIONS_PATH = "classpath:META-INF/sqlmaps/oracle/*.xml";
		} else {
			MAPPER_LOCATIONS_PATH = "classpath:META-INF/sqlmaps/altibase/*.xml";
		}
		 */
		sessionFactoryBean.setMapperLocations(pathResolver.getResources(MAPPER_LOCATIONS_PATH));
	}
	
}