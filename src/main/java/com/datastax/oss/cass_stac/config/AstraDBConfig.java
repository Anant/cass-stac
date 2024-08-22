package com.datastax.oss.cass_stac.config;

import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import lombok.Data;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.autoconfigure.cassandra.DriverConfigLoaderBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Objects;

@Configuration
@Data
public class AstraDBConfig {


    @Bean
    public DriverConfigLoaderBuilderCustomizer defaultProfile() {
        return builder -> builder
                .withDuration(DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofMillis(60000))
                .withDuration(DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT, Duration.ofMillis(60000))
                .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofMillis(60000))
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofMillis(60000))
                .build();
    }

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(final DataStaxAstraProperties astraProperties) {
        final String secureBundleFile = astraProperties.getSecureConnectBundle();
        final String username = astraProperties.getUsername();
        final String password = astraProperties.getPassword();
        final String keyspace = astraProperties.getKeyspace();
        return builder -> builder.withCloudSecureConnectBundle(Objects.requireNonNull(this.getClass().getResourceAsStream("/" + secureBundleFile)))
                .withAuthCredentials(username, password)
                .withKeyspace(keyspace);
    }

}