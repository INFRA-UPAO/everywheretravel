package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViajeroConPersonaResponseDTO {
    private Integer id;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String residencia;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private PersonaNaturalSinViajeroResponseDTO personaNatural;
}