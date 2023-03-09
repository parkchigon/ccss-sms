package com.lgu.ccss.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lgu.ccss.config.annontation.Master;

@Configuration
@MapperScan(basePackages = MyBatisConfig.BASE_PACKAGE, annotationClass = Master.class, sqlSessionFactoryRef = "masterSqlSessionFactory")
class MasterMyBatisConfig extends MyBatisConfig {

	@Bean
	public SqlSessionFactory masterSqlSessionFactory(
			@Qualifier("masterDataSource") DataSource masterDataSource) throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		configureSqlSessionFactoryMaster(sessionFactoryBean, masterDataSource);
		return sessionFactoryBean.getObject();
	}
}