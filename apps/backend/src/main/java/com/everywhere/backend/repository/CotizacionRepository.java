package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {
    @Query("SELECT MAX(c.id) FROM Cotizacion c")
    Integer findMaxId();

    @Query("SELECT c FROM Cotizacion c WHERE c.id NOT IN (SELECT l.cotizacion.id FROM Liquidacion l WHERE l.cotizacion IS NOT NULL)")
    List<Cotizacion> findCotizacionesSinLiquidacion();

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.formaPago.id = :formaPagoId")
    long countByFormaPagoId(@Param("formaPagoId") Integer formaPagoId);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.estadoCotizacion.id = :estado")
    long countByEstadoCotizacionId(@Param("estado") int estado);

    List<Cotizacion> findByid(int id);

    // Métodos para gestión de carpetas
    List<Cotizacion> findByCarpetaId(Integer carpetaId);

    List<Cotizacion> findByCarpetaIsNull();
}