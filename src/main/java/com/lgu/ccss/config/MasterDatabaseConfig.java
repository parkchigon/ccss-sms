package com.lgu.ccss.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
class MasterDatabaseConfig extends DatabaseConfig {

	private static final Logger logger = LoggerFactory.getLogger(MasterDatabaseConfig.class);

	@Value("#{datasource['jdbc.use.type']}")
	private Integer dbType;

	@Value("#{datasource['jdbc.db.driverClassName']}")
	private String jdbcDriverClassName; // org.mariadb.jdbc.Driver

	@Value("#{datasource['jdbc.db.url']}")
	private String jdbcDbUrl; // jdbc:mariadb://localhost:3306/parsingapp

	@Value("#{datasource['jdbc.db.userName']}")
	private String jdbcUserName; // ttkon

	@Value("#{datasource['jdbc.db.userPassword']}")
	private String jdbcUserPassword; // ttkon

	@Value("#{datasource['jdbc.db.initialSize']}")
	private int initialSize;

	@Value("#{datasource['jdbc.db.minIdle']}")
	private int minIdle;

	@Value("#{datasource['jdbc.db.maxIdle']}")
	private int maxIdle;

	@Value("#{datasource['jdbc.db.maxActive']}")
	private int maxActive;

	@Value("#{datasource['jdbc.db.maxWait']}")
	private long maxWait;

	@Value("#{datasource['jdbc.db.testWhileIdle']}")
	private boolean testWhileIdle;

	@Value("#{datasource['jdbc.db.testOnBorrow']}")
	private boolean testOnBorrow;

	@Value("#{datasource['jdbc.db.testOnReturn']}")
	private boolean testOnReturn;

	@Value("#{datasource['jdbc.db.numTestsPerEvictionRun']}")
	private int numTestsPerEvictionRun;

	@Value("#{datasource['jdbc.db.timeBetweenEvictionRunsMillis']}")
	private int timeBetweenEvictionRunsMillis;

	@Primary
	@Bean(name = "masterDataSource", destroyMethod = "close")
	public DataSource dataSource() {

		logger.info("[MasterDataSource jdbcDbUrl ------------------- ] : " + jdbcDbUrl);
		logger.info("[MasterDataSource dbType ---------------------- ] : " + dbType);
		logger.info("[MasterDataSource initialSize ----------------- ] : " + initialSize);
		logger.info("[MasterDataSource minIdle --------------------- ] : " + minIdle);
		logger.info("[MasterDataSource maxIdle --------------------- ] : " + maxIdle);
		logger.info("[MasterDataSource maxActive ------------------- ] : " + maxActive);
		logger.info("[MasterDataSource maxWait --------------------- ] : " + maxWait);
		logger.info("[MasterDataSource testWhileIdle --------------- ] : " + testWhileIdle);
		logger.info("[MasterDataSource testOnBorrow ---------------- ] : " + testOnBorrow);
		logger.info("[MasterDataSource testOnReturn ---------------- ] : " + testOnReturn);
		logger.info("[MasterDataSource numTestsPerEvictionRun ------ ] : " + numTestsPerEvictionRun);
		logger.info("[MasterDataSource timeBetweenEvictionRunsMillis ] : " + timeBetweenEvictionRunsMillis);

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(getDriverName(dbType));// jdbcDriverClassName);
		dataSource.setUrl(jdbcDbUrl);
		dataSource.setUsername(jdbcUserName);
		dataSource.setPassword(jdbcUserPassword);
		dataSource.setInitialSize(initialSize);
		dataSource.setMinIdle(minIdle);
		dataSource.setMaxIdle(maxIdle);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);
		dataSource.setValidationQuery(getValidationQuery(dbType));
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
//		dataSource.setTestOnReturn(testOnReturn);
//		dataSource.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

		// DataSource masterDataSource = new DataSource();
		// configureDataSource(dataSource, masterDatabaseProperties);
		// return masterDataSource;
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(@Qualifier("masterDataSource") DataSource masterDataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
		transactionManager.setGlobalRollbackOnParticipationFailure(false);
		return transactionManager;
	}

}