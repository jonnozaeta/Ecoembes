package es.deusto.sd.ecoembes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecoembesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ecoembes Server API")
                        .description("API REST para el prototipo 1 del Servidor Ecoembes - Sistemas Distribuidos (Universidad de Deusto)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo SD Ecoembes")
                                .email("soporte@ecoembes.es"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
