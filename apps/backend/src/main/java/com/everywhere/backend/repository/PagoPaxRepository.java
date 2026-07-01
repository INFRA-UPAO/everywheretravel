package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PagoPax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoPaxRepository extends JpaRepository<PagoPax, Integer> {

    @Query("SELECT p FROM PagoPax p LEFT JOIN FETCH p.liquidacion LEFT JOIN FETCH p.formaPago WHERE p.id = :id")
    Optional<PagoPax> findByIdWithRelations(Integer id);

    @Query("SELECT p FROM PagoPax p LEFT JOIN FETCH p.liquidacion LEFT JOIN FETCH p.formaPago")
    List<PagoPax> findAllWithRelations();

    @Query("SELECT p FROM PagoPax p WHERE p.liquidacion.id = :liquidacionId")
    List<PagoPax> findByLiquidacionId(Integer liquidacionId);
}
