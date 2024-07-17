package com.datastax.oss.cass_stac.config;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class AstraDBConfig {
	

	
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