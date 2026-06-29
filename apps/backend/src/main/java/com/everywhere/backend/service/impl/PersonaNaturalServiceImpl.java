package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.CategoriaPersona;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.repository.CategoriaPersonaRepository;
import com.everywhere.backend.service.PersonaNaturalService;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.mapper.PersonaNaturalMapper;
import com.everywhere.backend.mapper.PersonaMapper;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaNaturalServiceImpl implements PersonaNaturalService {

    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaRepository personaRepository;
    private final ViajeroRepository viajeroRepository;
    private final CategoriaPersonaRepository categoriaPersonaRepository;
    private final PersonaNaturalMapper personaNaturalMapper;
    private final PersonaMapper personaMapper;

    @Override
    public List<PersonaNaturalResponseDTO> findAll() {
        return personaNaturalRepository.findAll().stream().map(personaNaturalMapper::toResponseDTO).toList();
    }

    @Override
    public PersonaNaturalResponseDTO findById(Integer id) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + id));
        return personaNaturalMapper.toResponseDTO(personaNatural);
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByDocumento(String documento) {
        Optional<PersonaNatural> personaNaturalOptional = personaNaturalRepository.findByDocumentoIgnoreCase(documento);
        if (personaNaturalOptional.isEmpty())
            return List.of();
        return List.of(personaNaturalMapper.toResponseDTO(personaNaturalOptional.get()));
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByNombres(String nombres) {
        List<PersonaNatural> personaNaturalList = personaNaturalRepository.findByNombresIgnoreAccents(nombres);
        return personaNaturalList.stream().map(personaNaturalMapper::toResponseDTO).toList();
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByApellidosPaternos(String apellidosPaternos) {
        List<PersonaNatural> personaNaturalList = personaNaturalRepository.findByApellidosPaternoIgnoreAccents(apellidosPaternos);
        if (personaNaturalList.isEmpty())
            throw new ResourceNotFoundException("No se encontraron personas naturales con apellidos paternos: " + apellidosPaternos);
        return personaNaturalList.stream().map(personaNaturalMapper::toResponseDTO).toList();
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByApellidosMaternos(String apellidosMaternos) {
        List<PersonaNatural> personaNaturalList = personaNaturalRepository.findByApellidosMaternoIgnoreAccents(apellidosMaternos);
        if (personaNaturalList.isEmpty())
            throw new ResourceNotFoundException("No se encontraron personas naturales con apellidos maternos: " + apellidosMaternos);
        return personaNaturalList.stream().map(personaNaturalMapper::toResponseDTO).toList();
    }

    @Override
    public PersonaNaturalResponseDTO save(PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        // Validar que no exista ya una persona con el mismo documento
        if (personaNaturalRequestDTO.getDocumento() != null && !personaNaturalRequestDTO.getDocumento().trim().isEmpty()) {
            if (personaNaturalRepository.findByDocumentoIgnoreCase(personaNaturalRequestDTO.getDocumento()).isPresent())
                throw new DataIntegrityViolationException("Ya existe una persona natural con el documento: " + personaNaturalRequestDTO.getDocumento());
        }

        // Crear la persona base
        Personas persona = (personaNaturalRequestDTO.getPersona() != null)
            ? personaMapper.toEntity(personaNaturalRequestDTO.getPersona())
            : new Personas();

        // Crear la persona natural
        PersonaNatural personaNatural = personaNaturalMapper.toEntity(personaNaturalRequestDTO);
        personaNatural.setPersonas(personaRepository.save(persona)); 

        if (personaNaturalRequestDTO.getViajeroId() != null) {
            Viajero viajero = viajeroRepository.findById(personaNaturalRequestDTO.getViajeroId())
                    .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + personaNaturalRequestDTO.getViajeroId()));
            personaNatural.setViajero(viajero);
        }

        if (personaNaturalRequestDTO.getCategoriaPersonaId() != null) {
            CategoriaPersona categoria = categoriaPersonaRepository.findById(personaNaturalRequestDTO.getCategoriaPersonaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + personaNaturalRequestDTO.getCategoriaPersonaId()));
            personaNatural.setCategoriaPersona(categoria);
        } 
        return personaNaturalMapper.toResponseDTO(personaNaturalRepository.save(personaNatural));
    }

    @Override
    public PersonaNaturalResponseDTO patch(Integer id, PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        // 🚀 OPTIMIZACIÓN 1: Validar existencia ANTES de buscar el objeto
        if (!personaNaturalRepository.existsById(id))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + id);

        // 🚀 OPTIMIZACIÓN 2: Si viene documento, validar duplicado ANTES de buscar el objeto completo
        if (personaNaturalRequestDTO.getDocumento() != null && 
            !personaNaturalRequestDTO.getDocumento().trim().isEmpty() &&
            personaNaturalRepository.findByDocumentoIgnoreCaseAndIdNot(personaNaturalRequestDTO.getDocumento(), id).isPresent()) {
            throw new BadRequestException("Ya existe otra persona natural con el documento: " + personaNaturalRequestDTO.getDocumento());
        }

        // 🚀 OPTIMIZACIÓN 3: Si viene categoría, validar existencia ANTES de buscar objetos
        if (personaNaturalRequestDTO.getCategoriaPersonaId() != null && 
            !categoriaPersonaRepository.existsById(personaNaturalRequestDTO.getCategoriaPersonaId())) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + personaNaturalRequestDTO.getCategoriaPersonaId());
        }

        // Solo ahora buscar los objetos para hacer el update
        PersonaNatural existingPersonaNatural = personaNaturalRepository.findById(id).get();
        personaNaturalMapper.updateEntityFromDTO(personaNaturalRequestDTO, existingPersonaNatural);
        
        // Manejar categoría si se proporciona
        if (personaNaturalRequestDTO.getCategoriaPersonaId() != null) {
            CategoriaPersona categoria = categoriaPersonaRepository.findById(personaNaturalRequestDTO.getCategoriaPersonaId()).get();
            existingPersonaNatural.setCategoriaPersona(categoria);
        } 
        return personaNaturalMapper.toResponseDTO(personaNaturalRepository.save(existingPersonaNatural));
    }

    @Override
    public void deleteById(Integer id) {
        if (!personaNaturalRepository.existsById(id))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + id);
        personaNaturalRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PersonaNaturalResponseDTO asociarViajero(Integer personaNaturalId, Integer viajeroId) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId));

        Viajero viajero = viajeroRepository.findById(viajeroId)
                .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + viajeroId));

        personaNatural.setViajero(viajero);
        return personaNaturalMapper.toResponseDTO(personaNaturalRepository.save(personaNatural));
    }

    @Override
    @Transactional
    public PersonaNaturalResponseDTO desasociarViajero(Integer personaNaturalId) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId));

        personaNatural.setViajero(null); 
        return personaNaturalMapper.toResponseDTO(personaNaturalRepository.save(personaNatural));
    }
}