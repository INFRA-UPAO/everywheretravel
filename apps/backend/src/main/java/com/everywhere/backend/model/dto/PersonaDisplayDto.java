package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDisplayDto {
    private Integer id;
    private String tipo;
    private String identificador;
    private String nombre;
}