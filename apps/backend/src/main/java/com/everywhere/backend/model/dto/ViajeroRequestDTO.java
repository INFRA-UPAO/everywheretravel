package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ViajeroRequestDTO {
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String residencia;
    private Integer personaNaturalId;
}