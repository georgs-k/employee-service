package com.emansy.employeeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Bean
    public Docket configuration(){
        Docket docket =  new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.emansy"))
                .paths(PathSelectors.ant("/error").negate())
                .build()
                .apiInfo(apiInfo());
        docket.useDefaultResponseMessages(false);
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Event Management System - Employee microservice API")
                .description("Employee microservice for Event Management System")
                .version("1.0")
                .build();
    }
}
