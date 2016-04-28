package eu.cityopt.config;

import java.util.TimeZone;

import org.springframework.beans.factory.InitializingBean;

public class Initialization implements InitializingBean{

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}
