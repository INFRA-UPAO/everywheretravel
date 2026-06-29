package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProveedorRequestDTO {
    @NotEmpty(message = "El proveedor obligatoriamente tiene que tener nombre")
    private String nombre;
    @NotEmpty(message = "El proveedor obligatoriamente tiene que tener nombreJuridico")
    private String nombreJuridico;
    @NotEmpty(message = "El proveedor obligatoriamente tiene que tener Ruc")
    private Integer ruc;
}