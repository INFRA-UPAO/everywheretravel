package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.ObservacionLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObservacionLiquidacionRepository extends JpaRepository<ObservacionLiquidacion, Long> {

    @Query("SELECT o FROM ObservacionLiquidacion o " +
           "LEFT JOIN FETCH o.liquidacion l " +
           "LEFT JOIN FETCH l.producto " +
           "LEFT JOIN FETCH l.formaPago " +
           "LEFT JOIN FETCH l.cotizacion " +
           "LEFT JOIN FETCH l.carpeta " +
           "WHERE o.id = :id")
    Optional<ObservacionLiquidacion> findByIdWithLiquidacion(@Param("id") Long id);

    @Query("SELECT o FROM ObservacionLiquidacion o " +
           "LEFT JOIN FETCH o.liquidacion l " +
           "LEFT JOIN FETCH l.producto " +
           "LEFT JOIN FETCH l.formaPago " +
           "LEFT JOIN FETCH l.cotizacion " +
           "LEFT JOIN FETCH l.carpeta")
    List<ObservacionLiquidacion> findAllWithLiquidacion();

    @Query("SELECT o FROM ObservacionLiquidacion o " +
           "LEFT JOIN FETCH o.liquidacion " +
           "WHERE o.liquidacion.id = :liquidacionId")
    List<ObservacionLiquidacion> findByLiquidacionId(@Param("liquidacionId") Integer liquidacionId);
}