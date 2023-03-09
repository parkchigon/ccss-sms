package com.lgu.ccss.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lgu.ccss.config.annontation.Slave;

@Configuration
@MapperScan(basePackages = MyBatisConfig.BASE_PACKAGE, annotationClass = Slave.class, sqlSessionFactoryRef = "slaveSqlSessionFactory")
class SlaveMyBatisConfig extends MyBatisConfig {

	@Bean
	public SqlSessionFactory slaveSqlSessionFactory(
			@Qualifier("slaveDataSource") DataSource slaveDataSource)	throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		configureSqlSessionFactoryMaster(sessionFactoryBean, slaveDataSource);
		return sessionFactoryBean.getObject();
	}
}