package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProductoRequestDTO {
    private String descripcion;
    @NotEmpty(message = "El Producto obligatoriamente tiene que tener un tipo")
    private String tipo;

}
