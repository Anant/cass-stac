package com.datastax.oss.cass_stac.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenAPIConfig.class);

    @Bean
    public OpenAPI customOpenAPI() {
        List<Tag> tags = List.of(
                new Tag().name("Item").description("The STAC Item to insert and get"),
                new Tag().name("Item Collection").description("The STAC Item to insert"),
                new Tag().name("Feature").description("The STAC Feature to insert and get")
        );

        logger.debug("Defining OpenAPI tags in the following order: {}", tags);

        return new OpenAPI()
                .info(new Info().title("API Documentation").version("1.0"))
                .tags(tags);
    }
}
