package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TelefonoPersonaRequestDTO {
    @NotBlank(message = "El número de teléfono no puede estar vacío")
    private String numero;
    @NotNull(message = "El código de país no puede ser nulo")
    private String codigoPais;
    @NotBlank(message = "El tipo de teléfono no puede estar vacío")
    private String tipo;
    private String descripcion;
}
