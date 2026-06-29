package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleDocumentoConPersonasDto;
import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.dto.DetalleDocumentoSearchDto;

import java.util.List;

public interface DetalleDocumentoService {
    DetalleDocumentoResponseDto findById(Integer id);
    DetalleDocumentoResponseDto save(DetalleDocumentoRequestDto detalleDocumentoRequestDto);
    DetalleDocumentoResponseDto update(Integer id, DetalleDocumentoRequestDto detalleDocumentoRequestDto);
    void delete(Integer id);
    List<DetalleDocumentoResponseDto> findByPersonaId(Integer personaId);
    List<DetalleDocumentoResponseDto> findAll();
    List<DetalleDocumentoResponseDto> findByDocumentoId(Integer documentoId);
    List<DetalleDocumentoResponseDto> findByNumero(String numero);
    List<DetalleDocumentoResponseDto> findByPersonaNaturalId(Integer personaNaturalId);
    List<DetalleDocumentoSearchDto> findByPersonaNaturalDocumentoPrefix(String prefijo);
    List<DetalleDocumentoConPersonasDto> findDocumentosConPersonas();
    List<DetalleDocumentoConPersonasDto> findDocumentosConPersonasByNumero(String numero);
}