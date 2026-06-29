package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
public class NaturalJuridicoRequestDTO {
    
    @NotNull(message = "El ID de la persona natural es obligatorio")
    private Integer personaNaturalId;
    
    @NotNull(message = "La lista de personas jurídicas es obligatoria")
    private List<Integer> personasJuridicasIds;
}