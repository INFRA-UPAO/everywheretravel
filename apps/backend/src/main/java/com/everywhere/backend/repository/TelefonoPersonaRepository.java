package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.TelefonoPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelefonoPersonaRepository extends JpaRepository<TelefonoPersona, Integer> {

    List<TelefonoPersona> findByNumeroContaining(String numero);
    List<TelefonoPersona> findByCodigoPais(String codigoPais);
    List<TelefonoPersona> findByPersonaId(Integer personaId);
    Optional<TelefonoPersona> findByIdAndPersonaId(Integer telefonoId, Integer personaId);
    boolean existsByIdAndPersonaId(Integer telefonoId, Integer personaId);
}
