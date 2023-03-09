package com.lgu.ccss.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;

public abstract class DatabaseConfig {

	@Bean
	public abstract DataSource dataSource();

	public String getDriverName(int type) {
		// #1:oracle, 2:altibase
		if (type == 1) {
			return "oracle.jdbc.driver.OracleDriver";
		} else {
			return "Altibase5.jdbc.driver.AltibaseDriver";
		}
	}

	public String getValidationQuery(int type) {
		// #1:oracle, 2:altibase
		if (type == 1) {
			return "select 1 from dual";
		} else {
			return "select 1 from dual";
		}
	}
}
