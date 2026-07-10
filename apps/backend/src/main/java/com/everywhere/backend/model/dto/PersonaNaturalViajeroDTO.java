package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class PersonaNaturalViajeroDTO {
    
    @NotNull(message = "El ID del viajero es obligatorio")
    private Integer viajeroId;
}