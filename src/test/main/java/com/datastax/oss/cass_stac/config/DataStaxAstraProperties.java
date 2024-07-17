package com.datastax.oss.cass_stac.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "datastax.astra")
@Data
public class DataStaxAstraProperties {
        private String secureConnectBundle;
        private String username;
        private String password;
        private String keyspace;
}
