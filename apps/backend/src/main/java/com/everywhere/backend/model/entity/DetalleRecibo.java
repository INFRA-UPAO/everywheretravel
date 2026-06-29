package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "detalle_recibo")
@Data
public class DetalleRecibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "det_recibo_id_int")
    private Integer id;

    @Column(name = "det_recibo_cant_int")
    private Integer cantidad;

    @Column(name = "det_recibo_desc_vac")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String descripcion;

    @Column(name = "det_recibo_prec_dc")
    private BigDecimal precio;

    @CreationTimestamp
    @Column(name = "det_recibo_cre_tmp")
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "det_recibo_upd_tmp")
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_id_int")
    @JsonBackReference
    private Recibo recibo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id_int")
    private Producto producto;
}