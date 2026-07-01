package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DocumentoCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoCobranzaRepository extends JpaRepository<DocumentoCobranza, Long> {

       // Busca el último documento de cobranza para generar el siguiente serie y
       // correlativo
       Optional<DocumentoCobranza> findTopByOrderByIdDesc();

       @Query("SELECT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.carpeta " +
                     "LEFT JOIN FETCH d.formaPago " +
                     "LEFT JOIN FETCH d.usuario " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.detalles " +
                     "LEFT JOIN FETCH d.cotizacion " +
                     "WHERE d.serie = :serie AND d.correlativo = :correlativo")
       Optional<DocumentoCobranza> findBySerieAndCorrelativo(@Param("serie") String serie,
                     @Param("correlativo") Integer correlativo);

       @Query("SELECT d FROM DocumentoCobranza d WHERE d.persona.id = :personaId")
       Optional<DocumentoCobranza> findByPersonaId(@Param("personaId") Long personaId);

       @Query("SELECT d FROM DocumentoCobranza d WHERE d.cotizacion.id = :cotizacionId")
       Optional<DocumentoCobranza> findByCotizacionId(@Param("cotizacionId") Integer cotizacionId);

       @Query("SELECT DISTINCT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.carpeta " +
                     "LEFT JOIN FETCH d.formaPago fp " +
                     "LEFT JOIN FETCH d.usuario " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.personaJuridica " +
                     "LEFT JOIN FETCH d.detalleDocumento " +
                     "LEFT JOIN FETCH d.cotizacion " +
                     "WHERE d.id = :id")
       Optional<DocumentoCobranza> findByIdWithRelations(@Param("id") Long id);

       @Query("SELECT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.carpeta " +
                     "LEFT JOIN FETCH d.formaPago " +
                     "LEFT JOIN FETCH d.usuario " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.cotizacion")
       List<DocumentoCobranza> findAllWithRelations();

       @Query("SELECT DISTINCT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.formaPago " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.personaJuridica " +
                     "LEFT JOIN FETCH d.cotizacion")
       List<DocumentoCobranza> findAllForListing();

       @Query("SELECT DISTINCT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.detalles det " +
                     "LEFT JOIN FETCH det.producto " +
                     "WHERE d.id = :id")
       Optional<DocumentoCobranza> findByIdWithDetalles(@Param("id") Long id);

       // Métodos para gestión de carpetas
       @Query("SELECT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.carpeta " +
                     "LEFT JOIN FETCH d.formaPago " +
                     "LEFT JOIN FETCH d.usuario " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.cotizacion " +
                     "WHERE d.carpeta.id = :carpetaId")
       List<DocumentoCobranza> findByCarpetaId(@Param("carpetaId") Integer carpetaId);

       @Query("SELECT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.carpeta " +
                     "LEFT JOIN FETCH d.formaPago " +
                     "LEFT JOIN FETCH d.usuario " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.cotizacion " +
                     "WHERE d.carpeta IS NULL")
       List<DocumentoCobranza> findByCarpetaIsNull();
}