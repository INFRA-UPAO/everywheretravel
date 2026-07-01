package com.everywhere.backend.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "observacion_liquidacion")
public class ObservacionLiquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "obliq_id_int")
    private Long id;

    @Column(name = "obliq_desc_vac")
    private String descripcion;

    @Column(name = "obliq_val_dc", precision = 38, scale = 2)
    private BigDecimal valor;

    @Column(name = "obliq_doc_vac")
    private String documento;

    @Column(name = "obliq_nro_doc_vac")
    private String numeroDocumento;

    @CreationTimestamp
    @Column(name = "obliq_fec_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "obliq_fec_upd_tmp", updatable = true)
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "liq_id_int", nullable = false)
    private Liquidacion liquidacion;
}
