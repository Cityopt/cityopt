package eu.cityopt.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;

@Configuration
@EnableWebMvc
@ComponentScan({"com.pluralsight","eu.cityopt"}) 
public class appConfig extends WebMvcConfigurerAdapter {

	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("language");
		return localeChangeInterceptor;
	}

	@Bean(name = "localeResolver")
	public LocaleResolver getLocaleResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("en"));
		return localeResolver;
	}

	@Bean(name = "messageSource")
	public ResourceBundleMessageSource getMessageSource() {
		ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
		bundleMessageSource.setBasename("messages");
		return bundleMessageSource;
	}

	@Bean
	public InternalResourceViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");

		return resolver;
	}

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/pdfs/**").addResourceLocations("pdfs");
		registry.addResourceHandler("/assets/**")
				.addResourceLocations("assets");
	}

	@Bean
	public BeanNameViewResolver getBeanNameViewResolver() {
		BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
		beanNameViewResolver.setOrder(0);

		return beanNameViewResolver;
	}

	@Bean
	public ContentNegotiatingViewResolver getContentNegotiatingViewResolver() {
		
		ContentNegotiatingViewResolver negotiating = new ContentNegotiatingViewResolver();
		
		HashMap<String, MediaType> mediaTypes = new HashMap<String, MediaType>();
		
		mediaTypes.put("json", MediaType.APPLICATION_JSON);		
		mediaTypes.put("xml", MediaType.APPLICATION_XML);
		
		ContentNegotiationStrategy pathExtensionContentNegotiationStrategy = new PathExtensionContentNegotiationStrategy(
				mediaTypes);
		
		ContentNegotiationStrategy headerContentNegotiationStrategy = new HeaderContentNegotiationStrategy();
		ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager(
				pathExtensionContentNegotiationStrategy,
				headerContentNegotiationStrategy);
		negotiating.setContentNegotiationManager(contentNegotiationManager);
		
		List<View> JsonView = new ArrayList<View>();

		org.springframework.oxm.xstream.XStreamMarshaller marshaller = new XStreamMarshaller();
		marshaller.setAutodetectAnnotations(true);
		MarshallingView marshallerView = new MarshallingView();
		marshallerView.setMarshaller(marshaller);		
		
		JsonView.add(new MappingJackson2JsonView());
		JsonView.add(marshallerView);
				
		negotiating.setDefaultViews(JsonView);
		negotiating.setOrder(2);
		return negotiating;
	}	
	

}
