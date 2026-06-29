package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "forma_pago")
public class FormaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id_int")
    private Integer id;

    @Column(name = "form_cod_int")
    private Integer codigo;

    @Column(name = "form_desc_vac", length = 200)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "form_cre_vac", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "form_upd_vac")
    private LocalDateTime fechaActualizacion;
}
