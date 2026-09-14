package pe.edu.upeu.jdrefrigeracion;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "JD Refrigeración API",
        version = "v1",
        description = "Sistema de Gestión Web JD Refrigeración S.A.C."))
public class OpenApiConfig {
}
