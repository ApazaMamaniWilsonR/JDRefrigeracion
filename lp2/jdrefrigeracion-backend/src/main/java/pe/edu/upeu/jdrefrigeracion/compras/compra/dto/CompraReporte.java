package pe.edu.upeu.jdrefrigeracion.compras.compra.dto;

import java.util.List;

public record CompraReporte(CompraAgregado agregado, List<CompraResumen> compras) {
}
