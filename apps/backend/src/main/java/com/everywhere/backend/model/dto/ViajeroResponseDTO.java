package com.everywhere.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ViajeroResponseDTO {
    private Integer id;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String residencia;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    
    @JsonBackReference("viajero-personaNatural")
    private PersonaNaturalResponseDTO personaNatural;
}