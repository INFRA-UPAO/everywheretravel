package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DocumentoRequestDto;
import com.everywhere.backend.model.dto.DocumentoResponseDto;
import com.everywhere.backend.model.entity.Documento;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentoMapper {

    private final ModelMapper modelMapper;

    public Documento toEntity(DocumentoRequestDto documentoRequestDto) {
        Documento documento = modelMapper.map(documentoRequestDto, Documento.class);
        return documento;
    }

    public DocumentoResponseDto toResponseDTO(Documento documento) {
        DocumentoResponseDto documentoResponseDto = modelMapper.map(documento, DocumentoResponseDto.class);
        return documentoResponseDto;
    }

    public void updateEntityFromDto(DocumentoRequestDto documentoRequestDto, Documento documento) {
        modelMapper.map(documentoRequestDto, documento);
    }
}