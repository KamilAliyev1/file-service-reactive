package com.fileservicereactive.secur;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.IOException;
import java.net.URL;
import java.text.ParseException;


@RequiredArgsConstructor
@Configuration
public class TokenConfig {

    private final TokenConfigProperties tokenConfigProperties;

    @Bean
    RSAKey rsaKey() throws IOException, ParseException {
        JWKSet jwkSet = JWKSet.load(new URL(tokenConfigProperties.getJwkUri()));
        RSAKey rsaKey = (RSAKey) jwkSet.getKeys().get(0);
        return rsaKey;
    }

}
