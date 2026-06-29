package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.CorreoPersona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorreoPersonaRepository extends JpaRepository<CorreoPersona,Integer> {
    List<CorreoPersona> findByPersonaId(Integer personaId);
    boolean existsByEmail(String email);

}
