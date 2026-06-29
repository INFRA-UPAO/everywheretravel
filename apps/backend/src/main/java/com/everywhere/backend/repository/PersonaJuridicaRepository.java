package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PersonaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaJuridicaRepository extends JpaRepository<PersonaJuridica, Integer> {
    Optional<PersonaJuridica> findByRucIgnoreCase(String ruc);

    @Query(value = "SELECT * FROM persona_juridica WHERE UPPER(TRANSLATE(per_jurd_razSocial_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:razonSocial, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaJuridica> findByRazonSocialIgnoreAccents(@Param("razonSocial") String razonSocial);

    // Método original mantenido para compatibilidad
    List<PersonaJuridica> findByRazonSocialIgnoreCase(String razonSocial);
    Optional<PersonaJuridica> findByPersonasId(Integer personaId);
    Optional<PersonaJuridica> findByRucIgnoreCaseAndIdNot(String ruc, Integer id);
}