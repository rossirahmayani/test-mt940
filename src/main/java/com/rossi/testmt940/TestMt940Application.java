package com.rossi.testmt940;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class TestMt940Application {

    public static void main(String[] args) {
        SpringApplication.run(TestMt940Application.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/"), req ->
                ServerResponse.temporaryRedirect(URI.create("swagger-ui.html")).build());
    }

}
