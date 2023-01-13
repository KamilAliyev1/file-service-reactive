package com.fileservicereactive.secur;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(
        prefix = "jwt"
)
public class TokenConfigProperties {

    private String validateUrl;

    private String issuer;

    private String jwkUri;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getJwkUri() {
        return jwkUri;
    }

    public void setJwkUri(String jwkUri) {
        this.jwkUri = jwkUri;
    }

    public String getValidateUrl() {
        return validateUrl;
    }

    public void setValidateUrl(String validateUrl) {
        this.validateUrl = validateUrl;
    }
}
