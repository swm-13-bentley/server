package com.schedch.mvp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("mvp")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI mvpOpenAPI() {
        Server server = new Server();
        server.setUrl("https://www.swm-bentley-server.link");
        server.setDescription("Mannatime MVP server url");
        return new OpenAPI()
                .info(new Info().title("Mannatime API")
                        .description("언제만나(Mannatime) API 명세서")
                        .version("ver-0.0.1"))
                .addServersItem(server);
    }
}
