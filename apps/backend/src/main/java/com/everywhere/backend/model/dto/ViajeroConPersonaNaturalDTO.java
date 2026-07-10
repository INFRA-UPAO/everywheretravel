package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ViajeroConPersonaNaturalDTO {
    private Integer id;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String residencia;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private PersonaNaturalSinViajeroDTO personaNatural;
}