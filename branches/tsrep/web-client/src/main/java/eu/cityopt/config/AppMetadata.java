package eu.cityopt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@PropertySources({
	@PropertySource("classpath:application.properties")})
public class AppMetadata {
	@Autowired Environment env;
	
	public String getVersion()
	{
		return env.getProperty("application.version");
	}

}
