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
                new Tag().name("Item").description("The STAC Item object is the most important object in a STAC system. An Item is the entity that contains metadata for a scene and links to the assets.\n\n" +
                        "Item objects are the leaf nodes for a graph of Catalog and Collection objects. See the overview document for more information about how these objects relate to each other."),
                new Tag().name("Item Collection").description("The STAC Item to insert")
//                new Tag().name("Feature").description("The STAC Feature to insert and get")
        );

        logger.debug("Defining OpenAPI tags in the following order: {}", tags);

        return new OpenAPI()
                .info(new Info().title("API Documentation").version("1.0"))
                .tags(tags);
    }
}
