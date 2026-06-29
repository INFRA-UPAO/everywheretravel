package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CorreoPersonaRequestDTO {
    @NotEmpty(message = "El correo electrónico no puede estar vacío")
    private String email;
    private String tipo;
}
