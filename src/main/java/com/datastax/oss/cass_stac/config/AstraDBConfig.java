package com.datastax.oss.cass_stac.config;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.autoconfigure.cassandra.DriverConfigLoaderBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.oss.driver.api.core.config.DefaultDriverOption;

import lombok.Data;

@Configuration
@Data
public class AstraDBConfig {
	

	
	@Bean
	public DriverConfigLoaderBuilderCustomizer defaultProfile() {
		return builder -> builder
				.withString(DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT, "5 seconds")
				.withString(DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, "5 seconds")
				.withString(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, "5 seconds")
				.withString(DefaultDriverOption.REQUEST_TIMEOUT, "5 seconds")
				.build();
	}
	
	@Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(final DataStaxAstraProperties astraProperties) {
		final String secureBundleFile = astraProperties.getSecureConnectBundle();
		final String username = astraProperties.getUsername();
		final String password = astraProperties.getPassword();
		final String keyspace = astraProperties.getKeyspace();
		return builder -> builder.withCloudSecureConnectBundle(this.getClass().getResourceAsStream("/"+secureBundleFile))
                                                    .withAuthCredentials(username, password)
                                                    .withKeyspace(keyspace);
    }

	
}