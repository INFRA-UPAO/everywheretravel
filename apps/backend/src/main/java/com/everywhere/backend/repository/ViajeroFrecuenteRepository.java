package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.ViajeroFrecuente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViajeroFrecuenteRepository extends JpaRepository<ViajeroFrecuente, Integer> {
    List<ViajeroFrecuente> findByViajero_Id(Integer viajeroId);
    boolean existsByAreolineaAndCodigo(String areolinea, String codigo);
}