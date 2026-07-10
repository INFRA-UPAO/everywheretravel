
package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "categorias_personas")
public class CategoriaPersona {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cat_per_id_int")
	private int id;

	@Column(name = "cat_per_nom_vac")
	private String nombre;

	@Column(name = "cat_per_desc_vac")
	private String descripcion;

    @CreationTimestamp
	@Column(name = "cat_per_cre_tmp", updatable = false)
	private LocalDateTime creado;

    @UpdateTimestamp
	@Column(name = "cat_per_upd_tmp")
	private LocalDateTime actualizado;

	@OneToMany(mappedBy = "categoriaPersona", fetch = FetchType.LAZY)
    private List<PersonaNatural> personasNaturales;
}