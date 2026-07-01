package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pago_pax")
public class PagoPax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pax_id_int")
    private Integer id;

    @Column(name = "pax_monto_dc", precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "pax_moned_vac", length = 10)
    private String moneda;

    @Column(name = "pax_detalle_vac", length = 500)
    private String detalle;

    @CreationTimestamp
    @Column(name = "pax_fec_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "pax_fec_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "liq_id_int")
    private Liquidacion liquidacion;

    @ManyToOne
    @JoinColumn(name = "form_id_int")
    private FormaPago formaPago;
}
