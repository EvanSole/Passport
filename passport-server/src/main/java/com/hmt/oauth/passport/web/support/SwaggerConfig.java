package com.hmt.oauth.passport.web.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Edwin on 2017/2/21.
 * http://127.0.0.1:8081/swagger-ui.html
 */
@Configuration // 配置注解
@EnableWebMvc // 启用mvc,非springboot框架需要引入注解@EnableWebMvc
@EnableSwagger2 // 启用Swagger注解，
public class SwaggerConfig extends WebMvcConfigurerAdapter {

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2).select()// 选择哪些路径和API会生成document
                //.apis(RequestHandlerSelectors.any())// 对所有api进行监控
                .apis(RequestHandlerSelectors.basePackage("com.hmt.oauth.passport.web.controller"))
                .paths(PathSelectors.any())// 对所有路径进行监控
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .version("V1.0")
                .title("Passport统一认证系统RESTful APIs接口文档")
                .description("Passport统一认证系统API文档")
                .contact(new Contact("Passport项目组", null, "edwin.wang@huamengtong.com"))
                .termsOfServiceUrl("http://www.huamengtong.cn/")
                .build();
    }

}
