package com.everywhere.backend.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleDocumentoRequestDto {
    @NotBlank(message = "El número de documento es obligatorio")
    private String numero;

    private LocalDate fechaEmision; // yyyy-MM-dd
    private LocalDate fechaVencimiento;

    @NotBlank(message = "El país de origen es obligatorio")
    private String origen;

    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer documentoId;

    @NotNull(message = "La persona natural es obligatoria")
    private Integer personaNaturalId;
}