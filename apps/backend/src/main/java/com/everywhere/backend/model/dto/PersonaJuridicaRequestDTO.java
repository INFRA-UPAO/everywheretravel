package com.everywhere.backend.model.dto;

import lombok.Data; 
import jakarta.validation.Valid;

@Data
public class PersonaJuridicaRequestDTO {
    private String ruc;
    private String razonSocial;

    @Valid
    private PersonaRequestDTO persona;
}
