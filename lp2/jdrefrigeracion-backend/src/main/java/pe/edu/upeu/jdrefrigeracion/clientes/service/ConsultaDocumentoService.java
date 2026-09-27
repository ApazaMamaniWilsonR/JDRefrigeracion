package pe.edu.upeu.jdrefrigeracion.clientes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.clientes.client.DocumentoApiClient;
import pe.edu.upeu.jdrefrigeracion.clientes.dto.ConsultaDocumentoResponse;

@Service
@RequiredArgsConstructor
public class ConsultaDocumentoService {

    private final DocumentoApiClient documentoApiClient;

    public ConsultaDocumentoResponse consultarDni(String dni) {
        validarDni(dni);
        return documentoApiClient.consultarDni(dni);
    }

    public ConsultaDocumentoResponse consultarRuc(String ruc) {
        validarRuc(ruc);
        return documentoApiClient.consultarRuc(ruc);
    }

    private void validarDni(String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos");
        }
    }

    private void validarRuc(String ruc) {
        if (ruc == null || !ruc.matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos");
        }
    }
}