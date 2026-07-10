package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class PersonaNaturalCategoriaDTO {
    
    @NotNull(message = "El ID de la categoría es obligatorio")
    private Integer categoriaId;
}