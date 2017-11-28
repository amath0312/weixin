package tech.amath.common.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
@EnableCaching
public class WebConfig {

	public WebConfig() {
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*").allowCredentials(false)
						.allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS").maxAge(3600);
			}

			@Override
			public void addInterceptors(InterceptorRegistry registry) {
			}

			@Override
			public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
			}

			@Override
			public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
				super.configureMessageConverters(converters);
				FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
				FastJsonConfig fastJsonConfig = new FastJsonConfig();
				fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
				List<MediaType> fastMediaTypes = new ArrayList<>();
				fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
				fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
				fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
				converters.add(fastJsonHttpMessageConverter);
			}
		};
	}
}