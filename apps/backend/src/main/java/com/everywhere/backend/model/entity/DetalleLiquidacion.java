package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "detalle_liquidacion")
public class DetalleLiquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dtliq_id_int")
    private Integer id;

    @Column(name = "dtliq_tick_vac")
    private String ticket;

    @Column(name = "dtliq_doc_cobr_vac")
    private String documentoCobro;

    @Column(name = "dtliq_cost_tick_dc")
    private BigDecimal costoTicket;

    @Column(name = "dtliq_carg_serv_dc")
    private BigDecimal cargoServicio;

    @Column(name = "dtliq_val_vent_dc")
    private BigDecimal valorVenta;

    @Column(name = "dtliq_fee_emision_vac")
    private String feeEmision;

    @Column(name = "dtliq_doc_fee_vac")
    private String documentoFee;

    @Column(name = "dtliq_comision_vac")
    private String comision;

    @Column(name = "dtliq_fac_comp_vac")
    private String facturaCompra;

    @Column(name = "dtliq_bol_fac_pasj_vac")
    private String boletaPasajero;

    @Column(name = "dtliq_mont_desct_dc")
    private BigDecimal montoDescuento;

    @Column(name = "dtliq_pag_pax_dol_dc")
    private BigDecimal pagoPaxUSD;

    @Column(name = "dtliq_pag_pax_sol_dc")
    private BigDecimal pagoPaxPEN;

    @CreationTimestamp
    @Column(name = "dtliq_fec_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "dtliq_fec_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "opr_id_int", nullable = true)
    private Operador operador;

    @ManyToOne
    @JoinColumn(name = "prov_id_int", nullable = true)
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "via_id_int", nullable = true)
    private Viajero viajero;

    @ManyToOne
    @JoinColumn(name = "prod_id_int", nullable = true)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "liq_id_int", nullable = false)
    private Liquidacion liquidacion;

}