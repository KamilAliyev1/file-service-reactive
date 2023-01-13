package com.fileservicereactive.secur;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class AuthorizationFilter implements WebFilter {

    private final SecurityContext securityContext;

    private final TokenService tokenService;

    private final TokenConfigProperties tokenConfigProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {



        HttpHeaders headers = exchange.getRequest().getHeaders();

        String authorizationHeader = headers.get("Authorization").get(0);

        if(Strings.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")){
            throw new AuthenticationException();
        }
        String token = authorizationHeader.replace("Bearer ","");

        Map<String ,Object > userDetailMap;

        try{
            userDetailMap = tokenService.verifyJws(token);
        } catch (JOSEException | ParseException e) {
            throw new AuthenticationException(e.getMessage());
        }

        RestTemplate restTemplate = new RestTemplate();

        var response = restTemplate.postForEntity(
                tokenConfigProperties.getValidateUrl()
                        +"?id="+userDetailMap.get("sub")
                        +"&token="+token
                ,null
                , Serializable.class
        );

        if(!response.getStatusCode().is2xxSuccessful())throw new TokenVerifyException("Token not valid");

        List<String > auths = (List<String>) userDetailMap.get("auths");

        List<? extends GrantedAuthority> grantedAuthorities = auths!=null?auths.stream().map(e->new SimpleGrantedAuthority(e)).collect(Collectors.toList()): Collections.EMPTY_LIST;

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetailMap.get("sub")
                , null
                , grantedAuthorities
        );

        securityContext.setAuthentication(authentication);

        return ReactiveSecurityContextHolder.getContext().doOnNext(
                securityContext -> {
                    securityContext.setAuthentication(authentication);
                }
                ).then(chain.filter(exchange));


    }

}
