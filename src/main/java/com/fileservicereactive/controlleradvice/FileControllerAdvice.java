package com.fileservicereactive.controlleradvice;


import com.fileservicereactive.secur.AuthenticationException;
import com.fileservicereactive.secur.TokenVerifyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class FileControllerAdvice {


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {TokenVerifyException.class
            , AuthenticationException.class
    })
    Mono<?> getMessage(Exception e){
        return Mono.just(e.getMessage()!=null?e.getMessage():"");
    }

}
