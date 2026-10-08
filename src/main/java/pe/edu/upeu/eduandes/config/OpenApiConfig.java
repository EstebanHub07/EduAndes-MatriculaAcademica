package pe.edu.upeu.eduandes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI matriculaApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Matrícula API")
                        .version("v1"));
    }
}
