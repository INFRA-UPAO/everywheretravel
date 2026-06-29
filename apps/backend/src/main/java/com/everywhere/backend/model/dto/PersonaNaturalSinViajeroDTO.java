package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaNaturalSinViajeroDTO {
    private Integer id;
    private String documento;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String sexo;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private PersonaResponseDTO persona;
    private CategoriaPersonaResponseDTO categoriaPersona;
    // SIN campo viajero para evitar referencia circular
}