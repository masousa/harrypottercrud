package br.com.teste.harryPotter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport{
	@Bean
	  public Docket localizacaoApi() {
	    return new Docket(DocumentationType.SWAGGER_2)
	        .select()
	        .apis(RequestHandlerSelectors.basePackage("br.com.teste.harryPotter"))
	        .build()
	        .apiInfo(metaData());

	  }

	  private ApiInfo metaData() {
	    return new ApiInfoBuilder()
	        .title("Teste utilizando Spring Boot REST API para a implementação dos personagens do Harry Potter")
	        .description("\"Teste utilizando Spring Boot REST API para a criação de personagens do Harry Potter \"")
	        .version("1.0.0")
	        
	        .build();
	  }

	  @Override
	  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("swagger-ui.html")
	        .addResourceLocations("classpath:/META-INF/resources/");

	    registry.addResourceHandler("/webjars/**")
	        .addResourceLocations("classpath:/META-INF/resources/webjars/");
	  }
	
	

}
