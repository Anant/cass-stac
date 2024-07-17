package com.datastax.oss.cass_stac.config;

public class ConfigException extends Exception {

    // Default constructor
    public ConfigException() {
        super();
    }

    // Constructor that accepts a message
    public ConfigException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public ConfigException(Throwable cause) {
        super(cause);
    }
}