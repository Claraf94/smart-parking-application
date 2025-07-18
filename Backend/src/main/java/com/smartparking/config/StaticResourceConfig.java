package com.smartparking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 1) Mapeia qualquer URL para ../frontend/
    registry
      .addResourceHandler("/**")
      .addResourceLocations("file:../frontend/");

    registry.addResourceHandler("/css/**").addResourceLocations("file:../frontend/css/");
    registry.addResourceHandler("/js/**").addResourceLocations("file:../frontend/js/");
    registry.addResourceHandler("/images/**").addResourceLocations("file:../frontend/images/");
  }
}