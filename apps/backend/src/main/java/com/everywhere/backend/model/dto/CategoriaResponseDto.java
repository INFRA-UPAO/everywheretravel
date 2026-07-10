package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoriaResponseDto {
	private int id;
	private String nombre;
	private LocalDateTime creado;
	private LocalDateTime actualizado;
}
