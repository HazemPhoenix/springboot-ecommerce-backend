package io.spring.training.boot.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final StorageProperties storageProperties;

    public WebMvcConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String bookImagesLocation = "file:" + storageProperties.getBookLocation();
        String authorImagesLocation = "file:" + storageProperties.getAuthorLocation();
        registry.addResourceHandler("/uploads/**").addResourceLocations(bookImagesLocation, authorImagesLocation);
    }
}
