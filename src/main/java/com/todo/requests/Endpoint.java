package com.todo.requests;

import com.todo.conf.Configuration;

public class Endpoint {
    private String endpoint;
    private String version;

    public Endpoint(String endpoint) {
        this.endpoint = endpoint;
        this.version = Configuration.getProperty("version");
    }

    public String build() {
        String finalEndpoint = endpoint;
        if (this.version != null) {
            finalEndpoint = finalEndpoint + "/" + version;
        }
        return finalEndpoint;
    }
}
