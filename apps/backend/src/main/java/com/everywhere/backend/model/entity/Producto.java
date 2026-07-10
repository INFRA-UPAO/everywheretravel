package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_id_int")
    private int id;

    @Column(name = "prod_desc_vac", length = 255)
    private String descripcion;

    @Column(name = "prod_tipo_vac", length = 100)
    private String tipo;

    @CreationTimestamp
    @Column(name = "prod_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "prod_upd_tmp")
    private LocalDateTime actualizado;
}
