package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentoCobranzaMapper {

    private final ModelMapper modelMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final DetalleDocumentoCobranzaMapper detalleDocumentoCobranzaMapper;

    @PostConstruct
    public void configureMapping() {
        modelMapper.typeMap(DocumentoCobranzaUpdateDTO.class, DocumentoCobranza.class).addMappings(mapper -> {
            mapper.skip(DocumentoCobranza::setDetalleDocumento);
            mapper.skip(DocumentoCobranza::setSucursal);
            mapper.skip(DocumentoCobranza::setPersonaJuridica);
        });
    }

    // maneja la lógica de mapeo desde cotización a documento de cobranza
    public DocumentoCobranza fromCotizacion(CotizacionConDetallesResponseDTO cotizacionConDetallesResponseDTO,
            String serie, Integer correlativo) {
        DocumentoCobranza documentoCobranza = new DocumentoCobranza();

        documentoCobranza.setSerie(serie);
        documentoCobranza.setCorrelativo(correlativo);
        documentoCobranza.setMoneda(cotizacionConDetallesResponseDTO.getMoneda());

        Cotizacion cotizacionEntity = new Cotizacion();
        cotizacionEntity.setId(cotizacionConDetallesResponseDTO.getId());
        documentoCobranza.setCotizacion(cotizacionEntity);

        if (cotizacionConDetallesResponseDTO.getPersonas() != null) {
            Personas persona = new Personas();
            persona.setId(cotizacionConDetallesResponseDTO.getPersonas().getId());
            documentoCobranza.setPersona(persona);
        }

        if (cotizacionConDetallesResponseDTO.getSucursal() != null) {
            Sucursal sucursal = new Sucursal();
            sucursal.setId(cotizacionConDetallesResponseDTO.getSucursal().getId());
            documentoCobranza.setSucursal(sucursal);
        }

        if (cotizacionConDetallesResponseDTO.getFormaPago() != null) {
            FormaPago formaPago = new FormaPago();
            formaPago.setId(cotizacionConDetallesResponseDTO.getFormaPago().getId());
            documentoCobranza.setFormaPago(formaPago);
        }
        return documentoCobranza;
    }

    public void updateEntityFromUpdateDTO(DocumentoCobranza documentoCobranza,
            DocumentoCobranzaUpdateDTO documentoCobranzaUpdateDTO) {
        modelMapper.map(documentoCobranzaUpdateDTO, documentoCobranza);
    }

    public DocumentoCobranzaResponseDTO toResponseDTO(DocumentoCobranza documentoCobranza) {
        DocumentoCobranzaResponseDTO documentoCobranzaResponseDTO = modelMapper.map(documentoCobranza,
                DocumentoCobranzaResponseDTO.class);

        // Mapear código de cotización
        if (documentoCobranza.getCotizacion() != null) {
            documentoCobranzaResponseDTO.setCotizacionId(documentoCobranza.getCotizacion().getId());
            if (documentoCobranza.getCotizacion().getCodigoCotizacion() != null) {
                documentoCobranzaResponseDTO
                        .setCodigoCotizacion(documentoCobranza.getCotizacion().getCodigoCotizacion());
            }
        }

        if (documentoCobranza.getDetalleDocumento() != null) {
            documentoCobranzaResponseDTO.setDetalleDocumentoId(documentoCobranza.getDetalleDocumento().getId());
            documentoCobranzaResponseDTO.setClienteDocumento(documentoCobranza.getDetalleDocumento().getNumero());
            if (documentoCobranza.getDetalleDocumento().getDocumento() != null)
                documentoCobranzaResponseDTO
                        .setTipoDocumentoCliente(documentoCobranza.getDetalleDocumento().getDocumento().getTipo());
        }

        // Siempre setear personaId si existe
        if (documentoCobranza.getPersona() != null) {
            documentoCobranzaResponseDTO.setPersonaId(documentoCobranza.getPersona().getId());
        }

        // PRIORIDAD 1: Si hay PersonaJuridica seleccionada, usar sus datos
        if (documentoCobranza.getPersonaJuridica() != null) {
            PersonaJuridica pj = documentoCobranza.getPersonaJuridica();
            documentoCobranzaResponseDTO.setPersonaJuridicaId(pj.getId());
            documentoCobranzaResponseDTO.setPersonaJuridicaRuc(pj.getRuc());
            documentoCobranzaResponseDTO.setPersonaJuridicaRazonSocial(pj.getRazonSocial());

            // Usar datos de PersonaJuridica para el cliente
            documentoCobranzaResponseDTO.setClienteNombre(pj.getRazonSocial()); // Señores: razón social de la empresa
            documentoCobranzaResponseDTO.setClienteDocumento(pj.getRuc()); // Documento: RUC de la empresa
            documentoCobranzaResponseDTO.setTipoDocumentoCliente("RUC");
        }
        // PRIORIDAD 2: Si no hay PersonaJuridica, usar datos de Persona (Natural o
        // Jurídica base)
        else if (documentoCobranza.getPersona() != null) {
            Integer personaId = documentoCobranza.getPersona().getId();

            PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId).orElse(null);
            if (personaNatural != null) {
                // Concatenación null-safe para evitar mostrar "null" en el nombre
                String nombreCompleto = String.join(" ",
                        personaNatural.getNombres() != null ? personaNatural.getNombres().trim() : "",
                        personaNatural.getApellidosPaterno() != null ? personaNatural.getApellidosPaterno().trim() : "",
                        personaNatural.getApellidosMaterno() != null ? personaNatural.getApellidosMaterno().trim() : "")
                        .trim();
                documentoCobranzaResponseDTO.setClienteNombre(nombreCompleto.isEmpty() ? "Sin nombre" : nombreCompleto);

                // Si no hay detalleDocumento seleccionado, usar el campo documento de
                // PersonaNatural (legacy)
                if (documentoCobranza.getDetalleDocumento() == null) {
                    documentoCobranzaResponseDTO.setClienteDocumento(personaNatural.getDocumento());
                    documentoCobranzaResponseDTO.setTipoDocumentoCliente("DNI");
                }
            } else {
                PersonaJuridica personaJuridica = personaJuridicaRepository.findByPersonasId(personaId).orElse(null);
                if (personaJuridica != null) {
                    documentoCobranzaResponseDTO.setPersonaJuridicaId(personaJuridica.getId());
                    documentoCobranzaResponseDTO.setPersonaJuridicaRuc(personaJuridica.getRuc());
                    documentoCobranzaResponseDTO.setPersonaJuridicaRazonSocial(personaJuridica.getRazonSocial());

                    documentoCobranzaResponseDTO.setClienteNombre(personaJuridica.getRazonSocial()); // Señores: razón  social de la empresa
                    documentoCobranzaResponseDTO.setClienteDocumento(personaJuridica.getRuc()); // Documento: RUC de la empresa
                    documentoCobranzaResponseDTO.setTipoDocumentoCliente("RUC");
                }
            }
        }

        if (documentoCobranza.getSucursal() != null) {
            documentoCobranzaResponseDTO.setSucursalId(documentoCobranza.getSucursal().getId());
            documentoCobranzaResponseDTO.setSucursalDescripcion(documentoCobranza.getSucursal().getDescripcion());
        }
        if (documentoCobranza.getFormaPago() != null) {
            documentoCobranzaResponseDTO.setFormaPagoId(documentoCobranza.getFormaPago().getId());
            documentoCobranzaResponseDTO.setFormaPagoDescripcion(documentoCobranza.getFormaPago().getDescripcion());
        }

        // Mapear los detalles con el DetalleDocumentoCobranzaMapper
        if (documentoCobranza.getDetalles() != null && !documentoCobranza.getDetalles().isEmpty()) {
            List<DetalleDocumentoCobranzaResponseDTO> detallesDTO = documentoCobranza.getDetalles().stream()
                    .map(detalleDocumentoCobranzaMapper::toResponseDTO).toList();
            documentoCobranzaResponseDTO.setDetalles(detallesDTO);

            System.out.println("Mapeados " + detallesDTO.size() + " detalles para documento " + documentoCobranza.getId());
        }
        return documentoCobranzaResponseDTO;
    }
}