package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;

@Data
public class FormaPagoRequestDTO {
    
    @Min(value = 1, message = "El código debe ser mayor a 0")
    private Integer codigo;
    
    private String descripcion;
}
