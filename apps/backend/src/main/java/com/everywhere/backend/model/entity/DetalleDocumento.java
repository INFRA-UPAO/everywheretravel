package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name= "detalle_documento")
public class DetalleDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dtdoc_id_int")
    private Integer id;

    @Column(name = "dtdoc_numero_vac")
    private String numero;

    @Column(name = "dtdoc_fec_emi_tmp")
    private LocalDate fechaEmision;

    @Column(name = "dtdoc_fec_ven_tmp")
    private LocalDate fechaVencimiento;

    @CreationTimestamp
    @Column(name = "dtdoc_cre_tmp")
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "dtdoc_upd_tmp")
    private LocalDateTime actualizado;

    @Column(name = "dtdoc_ori_vac")
    private String origen;

    @ManyToOne
    @JoinColumn(name = "doc_id_int")
    private Documento documento;

    @ManyToOne
    @JoinColumn(name = "per_nat_id_int")
    private PersonaNatural personaNatural;
}