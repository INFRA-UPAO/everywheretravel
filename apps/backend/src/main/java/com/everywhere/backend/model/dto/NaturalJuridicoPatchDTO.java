package com.everywhere.backend.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class NaturalJuridicoPatchDTO {
    private List<Integer> agregar; // IDs de personas jurídicas a agregar
    private List<Integer> eliminar; // IDs de personas jurídicas a eliminar
}