package csc.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfigure implements WebMvcConfigurer {
    @Value("${CONSTANT.base_uri}")
    String start_path;
    @Override
    public void addViewControllers( ViewControllerRegistry registry ) {
        registry.addRedirectViewController("/", start_path);
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
    }

    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(new AuthCodeInterceptor());
    }

    @Bean
     public RequestContextListener requestContextListener(){
        return new RequestContextListener();
     }
}