package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.NaturalJuridico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NaturalJuridicoRepository extends JpaRepository<NaturalJuridico, Integer> {

    @Query("SELECT nj FROM NaturalJuridico nj WHERE nj.personaNatural.id = :personaNaturalId")
    List<NaturalJuridico> findByPersonaNaturalId(@Param("personaNaturalId") Integer personaNaturalId);

    @Query("SELECT nj FROM NaturalJuridico nj WHERE nj.personaJuridica.id = :personaJuridicaId")
    List<NaturalJuridico> findByPersonaJuridicaId(@Param("personaJuridicaId") Integer personaJuridicaId);

    @Query("SELECT nj FROM NaturalJuridico nj WHERE nj.personaNatural.id = :personaNaturalId AND nj.personaJuridica.id = :personaJuridicaId")
    Optional<NaturalJuridico> findByPersonaNaturalIdAndPersonaJuridicaId( // Verificar si ya existe una relación específica
        @Param("personaNaturalId") Integer personaNaturalId, 
        @Param("personaJuridicaId") Integer personaJuridicaId
    );

    void deleteByPersonaNaturalIdAndPersonaJuridicaId(Integer personaNaturalId, Integer personaJuridicaId);
}