package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleDocumentoRepository extends JpaRepository<DetalleDocumento, Integer> {
    List<DetalleDocumento> findByDocumentoId(Integer documentoId);
    List<DetalleDocumento> findByNumeroContainingIgnoreCase(String numero);
    List<DetalleDocumento> findByPersonaNaturalId(Integer personaNaturalId);
    List<DetalleDocumento> findByNumeroStartingWithIgnoreCase(String numero);
    
    @Query("SELECT DISTINCT dd FROM DetalleDocumento dd " +
           "LEFT JOIN FETCH dd.documento " +
           "LEFT JOIN FETCH dd.personaNatural pn " +
           "LEFT JOIN FETCH pn.personas")
    List<DetalleDocumento> findAllWithPersonasAndDocumento();
    
    @Query("SELECT DISTINCT dd FROM DetalleDocumento dd " +
           "LEFT JOIN FETCH dd.documento " +
           "LEFT JOIN FETCH dd.personaNatural pn " +
           "LEFT JOIN FETCH pn.personas " +
           "WHERE LOWER(dd.numero) LIKE LOWER(CONCAT(:numero, '%'))")
    List<DetalleDocumento> findByNumeroContainingWithPersonasAndDocumento(String numero);
}