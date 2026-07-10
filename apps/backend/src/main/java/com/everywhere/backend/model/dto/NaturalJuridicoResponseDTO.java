package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NaturalJuridicoResponseDTO {
    
    private Integer id;
    private PersonaNaturalResponseDTO personaNatural;
    private PersonaJuridicaResponseDTO personaJuridica;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}