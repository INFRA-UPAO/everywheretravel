package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleDocumentoCobranzaRepository extends JpaRepository<DetalleDocumentoCobranza, Long> {

    @Query("SELECT d FROM DetalleDocumentoCobranza d WHERE d.documentoCobranza.id = :documentoId")
    List<DetalleDocumentoCobranza> findByDocumentoCobranzaId(Long documentoId);
    
    @Query("SELECT d FROM DetalleDocumentoCobranza d WHERE d.producto.id = :productoId")
    List<DetalleDocumentoCobranza> findByProductoId(Long productoId);

    // Métodos sin lazy loading
    @Query("SELECT DISTINCT d FROM DetalleDocumentoCobranza d " +
           "LEFT JOIN FETCH d.documentoCobranza " +
           "LEFT JOIN FETCH d.producto")
    List<DetalleDocumentoCobranza> findAllWithRelations();

    @Query("SELECT DISTINCT d FROM DetalleDocumentoCobranza d " +
           "LEFT JOIN FETCH d.documentoCobranza " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE d.id = :id")
    Optional<DetalleDocumentoCobranza> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT DISTINCT d FROM DetalleDocumentoCobranza d " +
           "LEFT JOIN FETCH d.documentoCobranza " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE d.documentoCobranza.id = :documentoId")
    List<DetalleDocumentoCobranza> findByDocumentoCobranzaIdWithRelations(@Param("documentoId") Long documentoId);
}