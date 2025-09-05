package co.com.crediya.api.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.openapi")
@Getter
@Setter
public class OpenApiConfig {


    private String title;

    private String description;

    private String version;

    private String server;


    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name("Lina Ramirez"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .servers(List.of(
                        new Server().url(server).description("Local")
                ));
    }

    @Bean
    public GroupedOpenApi solicitudesApi() {
        return GroupedOpenApi.builder()
                .group("API Solicitudes")
                .pathsToMatch("/api/v1/solicitud/**")
                .build();
    }
    
}
