package pe.edu.upeu.jdrefrigeracion.clientes.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pe.edu.upeu.jdrefrigeracion.clientes.dto.ConsultaDocumentoResponse;
import pe.edu.upeu.jdrefrigeracion.clientes.entity.TipoCliente;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DocumentoApiClient {

    @Value("${api.documentos.base-url}")
    private String baseUrl;

    @Value("${api.documentos.token}")
    private String token;

    public ConsultaDocumentoResponse consultarDni(String dni) {
        validarToken();

        try {
            Map<String, Object> response = RestClient.builder()
                    .baseUrl(baseUrl)
                    .build()
                    .post()
                    .uri("/dni")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(Map.of("dni", dni))
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> data = obtenerData(response);

            String numero = obtenerValor(data, "dni", "numero", "numeroDocumento");
            String nombres = obtenerValor(data, "nombres", "nombre");
            String apellidoPaterno = obtenerValor(data, "apellido_paterno", "apellidoPaterno");
            String apellidoMaterno = obtenerValor(data, "apellido_materno", "apellidoMaterno");

            String apellidos = juntarTexto(apellidoPaterno, apellidoMaterno);
            String nombreComercial = juntarTexto(nombres, apellidos);

            return new ConsultaDocumentoResponse(
                    TipoCliente.PERSONA,
                    numero != null ? numero : dni,
                    nombres,
                    apellidos,
                    null,
                    nombreComercial,
                    null,
                    null,
                    null
            );

        } catch (Exception e) {
            throw new IllegalStateException("Error al consultar DNI en API Perú: " + e.getMessage());
        }
    }

    public ConsultaDocumentoResponse consultarRuc(String ruc) {
        validarToken();

        try {
            Map<String, Object> response = RestClient.builder()
                    .baseUrl(baseUrl)
                    .build()
                    .post()
                    .uri("/ruc")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(Map.of("ruc", ruc))
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> data = obtenerData(response);

            String numero = obtenerValor(data, "ruc", "numero", "numeroDocumento");
            String razonSocial = obtenerValor(data, "nombre_o_razon_social", "razon_social", "razonSocial", "nombre");
            String nombreComercial = obtenerValor(data, "nombre_comercial", "nombreComercial");
            String direccion = obtenerValor(data, "direccion", "direccion_completa", "direccionCompleta");
            String estado = obtenerValor(data, "estado", "estado_contribuyente", "estadoContribuyente");
            String condicion = obtenerValor(data, "condicion", "condicion_contribuyente", "condicionContribuyente");

            if (nombreComercial == null || nombreComercial.isBlank()) {
                nombreComercial = razonSocial;
            }

            return new ConsultaDocumentoResponse(
                    TipoCliente.EMPRESA,
                    numero != null ? numero : ruc,
                    null,
                    null,
                    razonSocial,
                    nombreComercial,
                    direccion,
                    estado,
                    condicion
            );

        } catch (Exception e) {
            throw new IllegalStateException("Error al consultar RUC en API Perú: " + e.getMessage());
        }
    }

    private void validarToken() {
        if (token == null
                || token.isBlank()
                || token.equals("TU_TOKEN_DE_API_PERU")
                || token.equals("PEGA_AQUI_TU_TOKEN")
                || token.equals("TU_TOKEN_AQUI")) {
            throw new IllegalStateException("Debes configurar un token real de API Perú en application.properties");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerData(Map<String, Object> response) {
        if (response == null || response.isEmpty()) {
            throw new IllegalArgumentException("La API no devolvió información");
        }

        Object success = response.get("success");

        if (success != null && success.toString().equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("La API respondió error: " + response.get("message"));
        }

        Object data = response.get("data");

        if (data instanceof Map<?, ?>) {
            return (Map<String, Object>) data;
        }

        return response;
    }

    private String obtenerValor(Map<String, Object> map, String... claves) {
        for (String clave : claves) {
            Object valor = map.get(clave);

            if (valor != null && !valor.toString().isBlank()) {
                return valor.toString();
            }
        }

        return null;
    }

    private String juntarTexto(String texto1, String texto2) {
        String parte1 = texto1 == null ? "" : texto1.trim();
        String parte2 = texto2 == null ? "" : texto2.trim();

        return (parte1 + " " + parte2).trim();
    }
}