package com.everywhere.backend.model.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class PersonaCotizacionId implements Serializable {
	private Integer personas; // Cambiar nombre y tipo para que coincida con PersonaCotizacion
	private Integer cotizacion; // Cambiar tipo a Integer
}