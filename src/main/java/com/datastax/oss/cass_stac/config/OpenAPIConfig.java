package com.datastax.oss.cass_stac.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenAPIConfig.class);

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        List<Tag> tags = List.of(
                new Tag().name("Item"),
                new Tag().name("Item Collection")
        );

        logger.debug("Defining OpenAPI tags in the following order: {}", tags);

        return new OpenAPI()
                .info(new Info().title("API Documentation").version("1.0"))
                .tags(tags);
    }
}
