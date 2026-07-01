package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "recibo")
@Data
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recibo_id_int")
    private Integer id;

    @Column(name = "recibo_serie_vac")
    private String serie;

    @Column(name = "recibo_corre_vac")
    private Integer correlativo;

    @Column(name = "recibo_fec_emi_tmp")
    private LocalDate fechaEmision;

    @Column(name = "recibo_obs_vac")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String observaciones;

    @Column(name = "recibo_fec_ven_tmp")
    private LocalDate fechaVencimiento;

    @Column(name = "recibo_file_ven_vac")
    private String fileVenta;

    @Column(name = "recibo_mon_vac")
    private String moneda;

    @CreationTimestamp
    @Column(name = "recibo_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "recibo_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "cot_id_int", unique = true)
    private Cotizacion cotizacion;

    @ManyToOne
    @JoinColumn(name = "carp_id_int")
    private Carpeta carpeta;

    @ManyToOne
    @JoinColumn(name = "form_id_int")
    private FormaPago formaPago;

    @ManyToOne
    @JoinColumn(name = "usr_id_int")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "suc_id_int")
    private Sucursal sucursal;

    @ManyToOne
    @JoinColumn(name = "per_id_int")
    private Personas persona;

    @ManyToOne
    @JoinColumn(name = "per_jur_id_int")
    private PersonaJuridica personaJuridica;

    @ManyToOne
    @JoinColumn(name = "dtdoc_id_int")
    private DetalleDocumento detalleDocumento;

    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetalleRecibo> detalleRecibo;

}