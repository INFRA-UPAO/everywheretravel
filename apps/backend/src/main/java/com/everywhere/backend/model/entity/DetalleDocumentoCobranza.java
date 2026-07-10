package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "detalle_documento_cobranza")
@Data
public class DetalleDocumentoCobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "det_doc_co_id_int")
    private Long id;

    @Column(name = "det_doc_co_cant_int")
    private Integer cantidad;

    @Column(name = "det_doc_co_desc_vac")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String descripcion;

    @Column(name = "det_doc_co_prec_dc")
    private BigDecimal precio;

    @CreationTimestamp
    @Column(name = "det_doc_co_cre_tmp")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_co_id_int")
    @JsonBackReference
    private DocumentoCobranza documentoCobranza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id_int")
    private Producto producto;
}