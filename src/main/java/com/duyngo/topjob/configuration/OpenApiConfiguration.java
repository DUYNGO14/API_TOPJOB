package com.duyngo.topjob.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    // thiết lập tên server
    private Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }

    // Thiệt lập thông tin
    private Contact createContact() {
        return new Contact()
                .email("ngoduy07142003@gmail.com")
                .name("Duy Ngô")
                .url("https://mail.google.com/mail/u/0/?tab=rm&ogbl#inbox");
    }

    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");
    }

    private Info createApiInfo() {
        return new Info()
                .title("API-TOP_JOB")
                .version("1.0")
                .contact(createContact())
                .description("This API top job")
                .termsOfService("https://mail.google.com/mail/u/0/?tab=rm&ogbl#inbox")
                .license(createLicense());
    }

    // thiết lập token
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(List.of(createServer("http://localhost:8080", "Server test")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder().group("api-service").packagesToScan("com.duyngo.topjob.controller").build();
    }
}
