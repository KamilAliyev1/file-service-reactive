package com.fileservicereactive.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
@ConfigurationProperties(prefix = "file-uri")
public class PathConfigProperties {


    private Map<String,String> general;

    private Map<String,String> secret;

    public PathConfigProperties() {
    }

    public Map<String, String> getGeneral() {
        return general;
    }

    public void setGeneral(Map<String, String> general) {
        this.general = general;
    }

    public Map<String, String> getSecret() {
        return secret;
    }

    public void setSecret(Map<String, String> secret) {
        this.secret = secret;
    }
}
