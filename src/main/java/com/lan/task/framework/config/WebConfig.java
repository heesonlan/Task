package com.lan.task.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.lan.task.framework.config.interceptors.LoginInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport{

	@Autowired
	private LoginInterceptor loginInterceptor;
	
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/*.html").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/");
		registry.addResourceHandler("/images/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/images/");
		registry.addResourceHandler("/javascript/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/javascript/");
		
		super.addResourceHandlers(registry);
	}
	
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
		.excludePathPatterns("/login.html", 
				"/images/*.*", 
				"/javascript/*.*",
				"/framework/login");
		super.addInterceptors(registry);
	}

}
