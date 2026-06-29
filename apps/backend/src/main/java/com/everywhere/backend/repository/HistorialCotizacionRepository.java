package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.HistorialCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialCotizacionRepository extends JpaRepository<HistorialCotizacion, Integer> {

    @Query("SELECT h FROM HistorialCotizacion h " +
           "LEFT JOIN FETCH h.usuario " +
           "LEFT JOIN FETCH h.cotizacion " +
           "LEFT JOIN FETCH h.estadoCotizacion " +
           "ORDER BY h.fechaCreacion DESC")
    List<HistorialCotizacion> findAllWithRelations();

    @Query("SELECT h FROM HistorialCotizacion h " +
           "LEFT JOIN FETCH h.usuario " +
           "LEFT JOIN FETCH h.cotizacion " +
           "LEFT JOIN FETCH h.estadoCotizacion " +
           "WHERE h.id = :id")
    Optional<HistorialCotizacion> findByIdWithRelations(@Param("id") Integer id);

    @Query("SELECT h FROM HistorialCotizacion h " +
           "LEFT JOIN FETCH h.usuario " +
           "LEFT JOIN FETCH h.estadoCotizacion " +
           "WHERE h.cotizacion.id = :cotizacionId " +
           "ORDER BY h.fechaCreacion DESC")
    List<HistorialCotizacion> findByCotizacionIdWithRelations(@Param("cotizacionId") Integer cotizacionId);
}