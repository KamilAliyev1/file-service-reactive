package com.fileservicereactive.secur;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.text.ParseException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TokenService {


    private final RSAKey rsaKey;

    private final TokenConfigProperties customTokenConfigProperties;

    public TokenService(RSAKey rsaKey, TokenConfigProperties customTokenConfigProperties) {
        this.rsaKey = rsaKey;
        this.customTokenConfigProperties = customTokenConfigProperties;
        log.info("TokenService CREATED");
    }


    public Map<String ,Object > verifyJws(String jwt) throws JOSEException, ParseException , TokenVerifyException {

        RSASSAVerifier rsassaVerifier = new RSASSAVerifier(rsaKey);

        SignedJWT signedJWT = SignedJWT.parse(jwt);

        var check = signedJWT.verify(rsassaVerifier);

        if(!check) throw new TokenVerifyException("illegal token");

        var iss = signedJWT.getJWTClaimsSet().getIssuer();

        var exp = signedJWT.getJWTClaimsSet().getExpirationTime();

        var sub = signedJWT.getJWTClaimsSet().getSubject();

        var nbf = signedJWT.getJWTClaimsSet().getNotBeforeTime();

        if(!iss.equals(customTokenConfigProperties.getIssuer()))throw new TokenVerifyException("token not from issuer");

        if(Instant.now().isBefore(nbf.toInstant()))throw new TokenVerifyException("nbf");

        if(Instant.now().isAfter(exp.toInstant()))throw new TokenVerifyException("exp");

        var auths  = signedJWT.getJWTClaimsSet().getStringArrayClaim("authorities");

        Map<String ,Object > map = new HashMap<>();

        map.put("sub",sub);

        map.put("auths",List.of(auths));

        return map;
    }


}
