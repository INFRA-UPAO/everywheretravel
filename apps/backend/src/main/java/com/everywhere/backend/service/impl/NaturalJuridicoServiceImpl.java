package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.NaturalJuridicoRequestDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoResponseDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoPatchDTO;
import com.everywhere.backend.model.entity.NaturalJuridico;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.repository.NaturalJuridicoRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.service.NaturalJuridicoService;

import com.everywhere.backend.exceptions.ResourceNotFoundException; 
import com.everywhere.backend.mapper.NaturalJuridicoMapper;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaturalJuridicoServiceImpl implements NaturalJuridicoService {

    private final NaturalJuridicoRepository naturalJuridicoRepository;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final NaturalJuridicoMapper naturalJuridicoMapper;

    @Override
    @Transactional
    public List<NaturalJuridicoResponseDTO> crearRelaciones(NaturalJuridicoRequestDTO naturalJuridicoRequestDTO) {
        if (!personaNaturalRepository.existsById(naturalJuridicoRequestDTO.getPersonaNaturalId()))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + naturalJuridicoRequestDTO.getPersonaNaturalId());

        for (Integer personaJuridicaId : naturalJuridicoRequestDTO.getPersonasJuridicasIds()) {
            if (!personaJuridicaRepository.existsById(personaJuridicaId))
                throw new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + personaJuridicaId);
                
            Optional<NaturalJuridico> naturalJuridicOptional = naturalJuridicoRepository
                    .findByPersonaNaturalIdAndPersonaJuridicaId(naturalJuridicoRequestDTO.getPersonaNaturalId(), personaJuridicaId);

            if (naturalJuridicOptional.isPresent())
                throw new DataIntegrityViolationException("Ya existe una relación entre la persona natural " + 
                    naturalJuridicoRequestDTO.getPersonaNaturalId() + " y la persona jurídica " + personaJuridicaId);
        }

        PersonaNatural personaNatural = personaNaturalRepository.findById(naturalJuridicoRequestDTO.getPersonaNaturalId()).get();
        List<NaturalJuridico> naturalJuridicoList = new ArrayList<>();

        for (Integer personaJuridicaId : naturalJuridicoRequestDTO.getPersonasJuridicasIds()) {
            PersonaJuridica personaJuridica = personaJuridicaRepository.findById(personaJuridicaId).get();

            NaturalJuridico nuevaRelacion = new NaturalJuridico();
            nuevaRelacion.setPersonaNatural(personaNatural);
            nuevaRelacion.setPersonaJuridica(personaJuridica);

            naturalJuridicoList.add(naturalJuridicoRepository.save(nuevaRelacion));
        }
        return mapToResponseList(naturalJuridicoList);
    }

    @Override
    public List<NaturalJuridicoResponseDTO> findByPersonaNaturalId(Integer personaNaturalId) {
        if (!personaNaturalRepository.existsById(personaNaturalId))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId); 
        return mapToResponseList(naturalJuridicoRepository.findByPersonaNaturalId(personaNaturalId));
    }

    @Override
    public List<NaturalJuridicoResponseDTO> findByPersonaJuridicaId(Integer personaJuridicaId) {
        if (!personaJuridicaRepository.existsById(personaJuridicaId))
            throw new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + personaJuridicaId);
        return mapToResponseList(naturalJuridicoRepository.findByPersonaJuridicaId(personaJuridicaId));
    }

    @Override
    public NaturalJuridicoResponseDTO findById(Integer id) {
        NaturalJuridico naturalJuridico = naturalJuridicoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada con ID: " + id));
        return naturalJuridicoMapper.toResponseDTO(naturalJuridico);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!naturalJuridicoRepository.existsById(id)) 
            throw new ResourceNotFoundException("Relación no encontrada con ID: " + id);
        naturalJuridicoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByPersonas(Integer personaNaturalId, Integer personaJuridicaId) {
        Optional<NaturalJuridico> naturalJuridicoOptional = naturalJuridicoRepository.findByPersonaNaturalIdAndPersonaJuridicaId(personaNaturalId, personaJuridicaId);
        if (naturalJuridicoOptional.isEmpty())
            throw new ResourceNotFoundException("No existe relación entre la persona natural " + personaNaturalId + " y la persona jurídica " + personaJuridicaId);      
        naturalJuridicoRepository.deleteByPersonaNaturalIdAndPersonaJuridicaId(personaNaturalId, personaJuridicaId);
    }

    @Override
    public List<NaturalJuridicoResponseDTO> findAll() { 
        return mapToResponseList(naturalJuridicoRepository.findAll());
    }

    @Override
    @Transactional
    public List<NaturalJuridicoResponseDTO> patchRelacionesPersonaNatural(Integer personaNaturalId, NaturalJuridicoPatchDTO naturalJuridicoPatchDTO) {
        if (!personaNaturalRepository.existsById(personaNaturalId))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId);

        if (naturalJuridicoPatchDTO.getAgregar() != null && !naturalJuridicoPatchDTO.getAgregar().isEmpty()) {
            for (Integer personaJuridicaId : naturalJuridicoPatchDTO.getAgregar()) {
                if (!personaJuridicaRepository.existsById(personaJuridicaId))
                    throw new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + personaJuridicaId);
            }
        }

        PersonaNatural personaNatural = null;

        if (naturalJuridicoPatchDTO.getEliminar() != null && !naturalJuridicoPatchDTO.getEliminar().isEmpty()) {
            for (Integer personaJuridicaId : naturalJuridicoPatchDTO.getEliminar()) {
                Optional<NaturalJuridico> relacionExistente = naturalJuridicoRepository
                        .findByPersonaNaturalIdAndPersonaJuridicaId(personaNaturalId, personaJuridicaId);

                if (relacionExistente.isPresent()) naturalJuridicoRepository.deleteById(relacionExistente.get().getId());
            }
        }

        if (naturalJuridicoPatchDTO.getAgregar() != null && !naturalJuridicoPatchDTO.getAgregar().isEmpty()) {
            if (personaNatural == null)
                personaNatural = personaNaturalRepository.findById(personaNaturalId).get();
            
            for (Integer personaJuridicaId : naturalJuridicoPatchDTO.getAgregar()) {
                Optional<NaturalJuridico> relacionExistente = naturalJuridicoRepository
                        .findByPersonaNaturalIdAndPersonaJuridicaId(personaNaturalId, personaJuridicaId);
                
                if (relacionExistente.isEmpty()) {
                    PersonaJuridica personaJuridica = personaJuridicaRepository.findById(personaJuridicaId).get();
                    
                    NaturalJuridico nuevaRelacion = new NaturalJuridico();
                    nuevaRelacion.setPersonaNatural(personaNatural);
                    nuevaRelacion.setPersonaJuridica(personaJuridica);
                    naturalJuridicoRepository.save(nuevaRelacion);
                }
            }
        }
        return findByPersonaNaturalId(personaNaturalId);
    }

    private List<NaturalJuridicoResponseDTO> mapToResponseList(List<NaturalJuridico> naturalJuridicos) {
        return naturalJuridicos.stream().map(naturalJuridicoMapper::toResponseDTO).toList();
    }
}