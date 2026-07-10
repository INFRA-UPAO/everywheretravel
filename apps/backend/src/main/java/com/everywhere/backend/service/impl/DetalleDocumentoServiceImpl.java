package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleDocumentoMapper;
import com.everywhere.backend.model.dto.DetalleDocumentoConPersonasDto;
import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.dto.DetalleDocumentoSearchDto;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.repository.DocumentoRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.service.DetalleDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleDocumentoServiceImpl implements DetalleDocumentoService {

    private final PersonaNaturalRepository personaNaturalRepository;
    private final DocumentoRepository documentoRepository;
    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final DetalleDocumentoMapper detalleDocumentoMapper;

    @Override
    public DetalleDocumentoResponseDto findById(Integer id) {
        DetalleDocumento detalle = detalleDocumentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleDocumento no encontrado con id: " + id));
        return detalleDocumentoMapper.toResponse(detalle);
    }

    @Override
    @Transactional
    public DetalleDocumentoResponseDto save(DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        // Crear entidad manualmente sin usar el mapper para documento y personaNatural
        DetalleDocumento detalleDocumento = new DetalleDocumento();
        detalleDocumento.setNumero(detalleDocumentoRequestDto.getNumero());
        detalleDocumento.setFechaEmision(detalleDocumentoRequestDto.getFechaEmision());
        detalleDocumento.setFechaVencimiento(detalleDocumentoRequestDto.getFechaVencimiento());
        detalleDocumento.setOrigen(detalleDocumentoRequestDto.getOrigen());

        // Mapear Documento
        if (detalleDocumentoRequestDto.getDocumentoId() != null) {
            if (!documentoRepository.existsById(detalleDocumentoRequestDto.getDocumentoId()))
                throw new ResourceNotFoundException("Documento no encontrado con id: " + detalleDocumentoRequestDto.getDocumentoId());
            detalleDocumento.setDocumento(documentoRepository.findById(detalleDocumentoRequestDto.getDocumentoId()).get());
        }

        // Mapear PersonaNatural y Viajero
        if (detalleDocumentoRequestDto.getPersonaNaturalId() != null) {
            if (!personaNaturalRepository.existsById(detalleDocumentoRequestDto.getPersonaNaturalId()))
                throw new ResourceNotFoundException("PersonaNatural no encontrado con id: " + detalleDocumentoRequestDto.getPersonaNaturalId());

            PersonaNatural personaNatural = personaNaturalRepository.findById(detalleDocumentoRequestDto.getPersonaNaturalId()).get();
            detalleDocumento.setPersonaNatural(personaNatural);

                    }

        return detalleDocumentoMapper.toResponse(detalleDocumentoRepository.save(detalleDocumento));
    }

    @Override
    @Transactional
    public DetalleDocumentoResponseDto update(Integer id, DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        if (!detalleDocumentoRepository.existsById(id))
            throw new ResourceNotFoundException("DetalleDocumento no encontrado con id: " + id);

        DetalleDocumento detalleDocumento = detalleDocumentoRepository.findById(id).get();

        // Actualizar campos simples
        detalleDocumento.setNumero(detalleDocumentoRequestDto.getNumero());
        detalleDocumento.setFechaEmision(detalleDocumentoRequestDto.getFechaEmision());
        detalleDocumento.setFechaVencimiento(detalleDocumentoRequestDto.getFechaVencimiento());
        detalleDocumento.setOrigen(detalleDocumentoRequestDto.getOrigen());

        // Mapear Documento
        if (detalleDocumentoRequestDto.getDocumentoId() != null) {
            if (!documentoRepository.existsById(detalleDocumentoRequestDto.getDocumentoId()))
                throw new ResourceNotFoundException("Documento no encontrado con id: " + detalleDocumentoRequestDto.getDocumentoId());
            detalleDocumento.setDocumento(documentoRepository.findById(detalleDocumentoRequestDto.getDocumentoId()).get());
        }

        // Mapear PersonaNatural y Viajero
        if (detalleDocumentoRequestDto.getPersonaNaturalId() != null) {
            if (!personaNaturalRepository.existsById(detalleDocumentoRequestDto.getPersonaNaturalId()))
                throw new ResourceNotFoundException("PersonaNatural no encontrado con id: " + detalleDocumentoRequestDto.getPersonaNaturalId());

            PersonaNatural personaNatural = personaNaturalRepository.findById(detalleDocumentoRequestDto.getPersonaNaturalId()).get();
            detalleDocumento.setPersonaNatural(personaNatural);
        }

        return detalleDocumentoMapper.toResponse(detalleDocumentoRepository.save(detalleDocumento));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!detalleDocumentoRepository.existsById(id)) 
            throw new ResourceNotFoundException("DetalleDocumento no encontrado con id: " + id);
        detalleDocumentoRepository.deleteById(id);
    }

    @Override
    public List<DetalleDocumentoResponseDto> findAll() {
        return mapToResponseList(detalleDocumentoRepository.findAll());
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByDocumentoId(Integer documentoId) {
        if (!documentoRepository.existsById(documentoId)) 
            throw new ResourceNotFoundException("Documento no encontrado con id: " + documentoId);
        return mapToResponseList(detalleDocumentoRepository.findByDocumentoId(documentoId));
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByNumero(String numero) {
        return mapToResponseList(detalleDocumentoRepository.findByNumeroContainingIgnoreCase(numero));
    }

    @Override
    public List<DetalleDocumentoResponseDto> findByPersonaNaturalId(Integer personaNaturalId) {
        if (!personaNaturalRepository.existsById(personaNaturalId)) 
            throw new ResourceNotFoundException("PersonaNatural no encontrada con id: " + personaNaturalId);
        return mapToResponseList(detalleDocumentoRepository.findByPersonaNaturalId(personaNaturalId));
    }

     @Override
    public List<DetalleDocumentoResponseDto> findByPersonaId(Integer personaId) {
        PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId)
            .orElseThrow(() -> new ResourceNotFoundException("PersonaNatural no encontrada con personaId: " + personaId));
        return mapToResponseList(detalleDocumentoRepository.findByPersonaNaturalId(personaNatural.getId()));
    }

    private List<DetalleDocumentoResponseDto> mapToResponseList(List<DetalleDocumento> detalles) {
        return detalles.stream().map(detalleDocumentoMapper::toResponse).toList();
    }

    @Override
    public List<DetalleDocumentoSearchDto> findByPersonaNaturalDocumentoPrefix(String prefijo) {
        if (prefijo == null || prefijo.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByNumeroStartingWithIgnoreCase(prefijo.trim());
        return detalles.stream()
                .map(detalle -> DetalleDocumentoSearchDto.builder()
                        .numero(detalle.getNumero())
                        .personasId(detalle.getPersonaNatural() != null ? detalle.getPersonaNatural().getPersonas().getId() : null)
                        .nombres(detalle.getPersonaNatural() != null ? detalle.getPersonaNatural().getNombres() : null)
                        .apellidosPaterno(detalle.getPersonaNatural() != null ? detalle.getPersonaNatural().getApellidosPaterno() : null)
                        .apellidosMaterno(detalle.getPersonaNatural() != null ? detalle.getPersonaNatural().getApellidosMaterno() : null)
                        .sexo(detalle.getPersonaNatural() != null ? detalle.getPersonaNatural().getSexo() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<DetalleDocumentoConPersonasDto> findDocumentosConPersonas() {
        // Usar query optimizada con JOIN FETCH para evitar problema N+1
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findAllWithPersonasAndDocumento();
        
        // Agrupar por número de documento (manejando números nulos)
        return detalles.stream()
                .filter(doc -> doc.getNumero() != null) // Filtrar documentos sin número
                .collect(Collectors.groupingBy(DetalleDocumento::getNumero))
                .entrySet().stream()
                .map(entry -> {
                    String numeroDocumento = entry.getKey();
                    List<DetalleDocumento> documentos = entry.getValue();
                    
                    // Obtener el tipo de documento (asumiendo que todos los documentos con el mismo número tienen el mismo tipo)
                    String tipoDocumento = documentos.isEmpty() || documentos.get(0).getDocumento() == null 
                            ? "Sin tipo" 
                            : documentos.get(0).getDocumento().getTipo();
                    
                    // Obtener la información de las personas (ID y nombre completo)
                    List<DetalleDocumentoConPersonasDto.PersonaInfo> personas = documentos.stream()
                            .filter(doc -> doc.getPersonaNatural() != null)
                            .filter(doc -> doc.getPersonaNatural().getPersonas() != null)
                            .map(doc -> {
                                PersonaNatural personaNatural = doc.getPersonaNatural();
                                Integer personaId = personaNatural.getPersonas().getId();
                                String nombreCompleto = String.format("%s %s %s", 
                                        personaNatural.getNombres() != null ? personaNatural.getNombres() : "",
                                        personaNatural.getApellidosPaterno() != null ? personaNatural.getApellidosPaterno() : "",
                                        personaNatural.getApellidosMaterno() != null ? personaNatural.getApellidosMaterno() : ""
                                ).trim();
                                return DetalleDocumentoConPersonasDto.PersonaInfo.builder()
                                        .personaId(personaId)
                                        .nombreCompleto(nombreCompleto)
                                        .build();
                            })
                            .filter(p -> !p.getNombreCompleto().isEmpty())
                            .distinct()
                            .collect(Collectors.toList());
                    
                    return DetalleDocumentoConPersonasDto.builder()
                            .numeroDocumento(numeroDocumento)
                            .tipoDocumento(tipoDocumento)
                            .personas(personas)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DetalleDocumentoConPersonasDto> findDocumentosConPersonasByNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Usar query optimizada con JOIN FETCH para evitar problema N+1
        List<DetalleDocumento> detalles = detalleDocumentoRepository.findByNumeroContainingWithPersonasAndDocumento(numero.trim());
        
        // Agrupar por número de documento (manejando números nulos)
        return detalles.stream()
                .filter(doc -> doc.getNumero() != null)
                .collect(Collectors.groupingBy(DetalleDocumento::getNumero))
                .entrySet().stream()
                .map(entry -> {
                    String numeroDocumento = entry.getKey();
                    List<DetalleDocumento> documentos = entry.getValue();
                    
                    // Obtener el tipo de documento
                    String tipoDocumento = documentos.isEmpty() || documentos.get(0).getDocumento() == null 
                            ? "Sin tipo" 
                            : documentos.get(0).getDocumento().getTipo();
                    
                    // Obtener la información de las personas (ID y nombre completo)
                    List<DetalleDocumentoConPersonasDto.PersonaInfo> personas = documentos.stream()
                            .filter(doc -> doc.getPersonaNatural() != null)
                            .filter(doc -> doc.getPersonaNatural().getPersonas() != null)
                            .map(doc -> {
                                PersonaNatural personaNatural = doc.getPersonaNatural();
                                Integer personaId = personaNatural.getPersonas().getId();
                                String nombreCompleto = String.format("%s %s %s", 
                                        personaNatural.getNombres() != null ? personaNatural.getNombres() : "",
                                        personaNatural.getApellidosPaterno() != null ? personaNatural.getApellidosPaterno() : "",
                                        personaNatural.getApellidosMaterno() != null ? personaNatural.getApellidosMaterno() : ""
                                ).trim();
                                return DetalleDocumentoConPersonasDto.PersonaInfo.builder()
                                        .personaId(personaId)
                                        .nombreCompleto(nombreCompleto)
                                        .build();
                            })
                            .filter(p -> !p.getNombreCompleto().isEmpty())
                            .distinct()
                            .collect(Collectors.toList());
                    
                    return DetalleDocumentoConPersonasDto.builder()
                            .numeroDocumento(numeroDocumento)
                            .tipoDocumento(tipoDocumento)
                            .personas(personas)
                            .build();
                })
                .collect(Collectors.toList());
    }
}