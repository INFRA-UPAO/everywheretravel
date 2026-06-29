package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "documentos")
@Data
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id_int" )
    private Integer id;

    @Column(name = "doc_tipo_vac", nullable = false, unique = true)
    private String tipo;

    @Column(name = "doc_desc_vac")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String descripcion;

    @Column(name = "doc_est_bln")
    private Boolean estado;

    @CreationTimestamp
    @Column(name = "doc_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "doc_upd_tmp")
    private LocalDateTime actualizado;
}