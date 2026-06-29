package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DocumentoCobranzaMapper;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.SucursalRepository;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.repository.DocumentoCobranzaRepository;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.repository.NaturalJuridicoRepository;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.security.SecurityContextHelper;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DetalleCotizacionService;
import com.everywhere.backend.service.DocumentoCobranzaService;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.model.entity.Producto;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoCobranzaServiceImpl implements DocumentoCobranzaService {

    private final CotizacionService cotizacionService;
    private final DocumentoCobranzaRepository documentoCobranzaRepository;
    private final DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;
    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final DocumentoCobranzaMapper documentoCobranzaMapper;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final SucursalRepository sucursalRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final NaturalJuridicoRepository naturalJuridicoRepository;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final DetalleCotizacionService detalleCotizacionService;
    private final CarpetaRepository carpetaRepository;
    private final com.everywhere.backend.util.pdf.DocumentoCobranzaPdfGenerator documentoCobranzaPdfGenerator;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public DocumentoCobranzaResponseDTO createDocumentoCobranza(Integer cotizacionId, Integer personaJuridicaId,
            Integer sucursalId) {
        if (cotizacionId == null)
            throw new IllegalArgumentException("El ID de la cotización no puede ser nulo");

        if (documentoCobranzaRepository.findByCotizacionId(cotizacionId).isPresent())
            throw new DataIntegrityViolationException(
                    "Ya existe un documento de cobranza para la cotización ID: " + cotizacionId);

        CotizacionConDetallesResponseDTO cotizacion = cotizacionService.findByIdWithDetalles(cotizacionId);

        String[] serieCorrelativo = generateNextDocumentNumber();
        System.out.println("=== GENERANDO DOCUMENTO ===");
        System.out.println("Serie: " + serieCorrelativo[0]);
        System.out.println("Correlativo: " + serieCorrelativo[1]);
        DocumentoCobranza documentoCobranza = documentoCobranzaMapper.fromCotizacion(cotizacion, serieCorrelativo[0],
                Integer.parseInt(serieCorrelativo[1]));

        // Validar y setear PersonaJuridica si fue proporcionada
        if (personaJuridicaId != null) {
            PersonaJuridica personaJuridica = personaJuridicaRepository.findById(personaJuridicaId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Persona jurídica no encontrada con ID: " + personaJuridicaId));

            // Validar que la PersonaJuridica esté asociada a la PersonaNatural de la cotización
            if (cotizacion.getPersonas() != null) {
                Integer personaId = cotizacion.getPersonas().getId();
                PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId)
                        .orElse(null);

                if (personaNatural != null) {
                    // Verificar que existe la relación NaturalJuridico
                    boolean relacionExiste = naturalJuridicoRepository
                            .findByPersonaNaturalIdAndPersonaJuridicaId(personaNatural.getId(), personaJuridicaId)
                            .isPresent();

                    if (!relacionExiste) {
                        throw new IllegalArgumentException("La persona jurídica ID " + personaJuridicaId +
                                " no está asociada a la persona natural de la cotización");
                    }
                }
            }

            documentoCobranza.setPersonaJuridica(personaJuridica);
        }

        // Validar y setear Sucursal si fue proporcionada
        if (sucursalId != null) {
            Sucursal sucursal = sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));
            documentoCobranza.setSucursal(sucursal);
        }

        documentoCobranza = documentoCobranzaRepository.save(documentoCobranza);

        // Crear detalles desde cotización con repartición por cantidad
        crearDetallesDesdeCotizacion(documentoCobranza, cotizacionId);

        return documentoCobranzaMapper.toResponseDTO(documentoCobranza);
    }

    @Override
    public ByteArrayInputStream generatePdf(Long documentoId) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findByIdWithRelations(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento de cobranza no encontrado con ID: " + documentoId));

        documentoCobranzaRepository.findByIdWithDetalles(documentoId)
                .ifPresent(d -> documentoCobranza.setDetalles(d.getDetalles()));

        DocumentoCobranzaResponseDTO documentoCobranzaResponseDTO = documentoCobranzaMapper
                .toResponseDTO(documentoCobranza);
        String userName = getAuthenticatedUserName();
        return documentoCobranzaPdfGenerator.generatePdf(documentoCobranzaResponseDTO, userName);
    }

    @Override
    public DocumentoCobranzaResponseDTO findById(Long id) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + id));
        return documentoCobranzaMapper.toResponseDTO(documentoCobranza);
    }

    @Override
    public DocumentoCobranzaResponseDTO findBySerieAndCorrelativo(String serie, Integer correlativo) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findBySerieAndCorrelativo(serie, correlativo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento de cobranza no encontrado con serie: " + serie + " y correlativo: " + correlativo));
        return documentoCobranzaMapper.toResponseDTO(documentoCobranza);
    }

    @Override
    public List<DocumentoCobranzaResponseDTO> findAll() {
        return mapToResponseList(documentoCobranzaRepository.findAllForListing());
    }

    @Override
    public DocumentoCobranzaResponseDTO findByCotizacionId(Integer cotizacionId) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findByCotizacionId(cotizacionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento de cobranza no encontrado para cotización ID: " + cotizacionId));
        return documentoCobranzaMapper.toResponseDTO(documentoCobranza);
    }

    @Override
    @Transactional
    public DocumentoCobranzaResponseDTO patchDocumento(Long id, DocumentoCobranzaUpdateDTO documentoCobranzaUpdateDTO) {
        if (!documentoCobranzaRepository.existsById(id))
            throw new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + id);

        if (documentoCobranzaUpdateDTO.getDetalleDocumentoId() != null &&
                !detalleDocumentoRepository.existsById(documentoCobranzaUpdateDTO.getDetalleDocumentoId()))
            throw new ResourceNotFoundException(
                    "Detalle de documento no encontrado con ID: " + documentoCobranzaUpdateDTO.getDetalleDocumentoId());

        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findById(id).get();
        documentoCobranzaMapper.updateEntityFromUpdateDTO(documentoCobranza, documentoCobranzaUpdateDTO);

        // ========== LÓGICA MUTUAMENTE EXCLUYENTE: DetalleDocumento XOR PersonaJuridica
        // ========== Si se envía detalleDocumentoId, usar documento personal y LIMPIAR empresa
        if (documentoCobranzaUpdateDTO.getDetalleDocumentoId() != null) {
            DetalleDocumento detalleDocumento = detalleDocumentoRepository
                    .findById(documentoCobranzaUpdateDTO.getDetalleDocumentoId()).get();
            documentoCobranza.setDetalleDocumento(detalleDocumento);
            // Limpiar PersonaJuridica cuando se selecciona documento personal
            documentoCobranza.setPersonaJuridica(null);
        }
        // Si se envía personaJuridicaId, usar empresa y LIMPIAR documento personal
        else if (documentoCobranzaUpdateDTO.getPersonaJuridicaId() != null) {
            PersonaJuridica personaJuridica = personaJuridicaRepository
                    .findById(documentoCobranzaUpdateDTO.getPersonaJuridicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: "
                            + documentoCobranzaUpdateDTO.getPersonaJuridicaId()));

            // Validar que la PersonaJuridica esté asociada a la PersonaNatural del documento
            if (documentoCobranza.getPersona() != null) {
                Integer personaId = documentoCobranza.getPersona().getId();
                PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId)
                        .orElse(null);

                if (personaNatural != null) {
                    boolean relacionExiste = naturalJuridicoRepository
                            .findByPersonaNaturalIdAndPersonaJuridicaId(personaNatural.getId(),
                                    documentoCobranzaUpdateDTO.getPersonaJuridicaId())
                            .isPresent();

                    if (!relacionExiste) {
                        throw new IllegalArgumentException(
                                "La persona jurídica ID " + documentoCobranzaUpdateDTO.getPersonaJuridicaId() +
                                        " no está asociada a la persona natural del documento");
                    }
                }
            }

            documentoCobranza.setPersonaJuridica(personaJuridica);
            // Limpiar DetalleDocumento cuando se selecciona empresa
            documentoCobranza.setDetalleDocumento(null);
        }

        // Validar y actualizar Sucursal si fue proporcionada
        if (documentoCobranzaUpdateDTO.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(documentoCobranzaUpdateDTO.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada con ID: " + documentoCobranzaUpdateDTO.getSucursalId()));
            documentoCobranza.setSucursal(sucursal);
        }

        // Validar y actualizar FormaPago si fue proporcionada
        if (documentoCobranzaUpdateDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(documentoCobranzaUpdateDTO.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Forma de pago no encontrada con ID: " + documentoCobranzaUpdateDTO.getFormaPagoId()));
            documentoCobranza.setFormaPago(formaPago);
            System.out.println("FormaPago actualizada: " + formaPago.getDescripcion());
        }

        return documentoCobranzaMapper.toResponseDTO(documentoCobranzaRepository.save(documentoCobranza));
    }

    // ========== MÉTODOS PRIVADOS ==========
    private String[] generateNextDocumentNumber() {
        Optional<DocumentoCobranza> lastDocOpt = documentoCobranzaRepository.findTopByOrderByIdDesc();

        if (lastDocOpt.isPresent()) {
            DocumentoCobranza lastDoc = lastDocOpt.get();
            String lastSerie = lastDoc.getSerie();
            Integer lastCorrelativo = lastDoc.getCorrelativo();

            // Si correlativo llegó al máximo, incrementa serie
            if (lastCorrelativo >= 999999999) {
                int serieNum = Integer.parseInt(lastSerie.substring(2)) + 1;
                return new String[] { "DC" + String.format("%02d", serieNum), "1" };
            }
            // Incrementa correlativo
            return new String[] { lastSerie, String.valueOf(lastCorrelativo + 1) };
        }

        // Primer documento
        return new String[] { "DC01", "1" };
    }

    private List<DocumentoCobranzaResponseDTO> mapToResponseList(List<DocumentoCobranza> documentos) {
        return documentos.stream().map(documentoCobranzaMapper::toResponseDTO).toList();
    }

    /**
     * Obtiene el nombre del usuario autenticado actualmente
     * 
     * @return Nombre del usuario autenticado o "Usuario desconocido" si no hay autenticación
     */
    private String getAuthenticatedUserName() {
        try {
            return securityContextHelper.getCurrentUser().getNombre();
        } catch (Exception e) {
            return "Usuario desconocido";
        }
    }

    private void crearDetallesDesdeCotizacion(DocumentoCobranza documentoCobranza, Integer cotizacionId) {
        // Obtener todos los detalles de la cotización
        List<DetalleCotizacionResponseDto> detallesCotizacion = detalleCotizacionService
                .findByCotizacionId(cotizacionId);

        // Filtrar solo los detalles seleccionados
        List<DetalleCotizacionResponseDto> detallesSeleccionados = detallesCotizacion.stream()
                .filter(detalle -> detalle.getSeleccionado() != null && detalle.getSeleccionado())
                .toList();

        // Por cada detalle seleccionado, crear N detalles de documento de cobranza (donde N = cantidad)
        for (DetalleCotizacionResponseDto detalleCot : detallesSeleccionados) {
            int cantidad = detalleCot.getCantidad() != null ? detalleCot.getCantidad() : 1;

            // Crear un detalle de documento de cobranza por cada unidad de cantidad
            for (int i = 0; i < cantidad; i++) {
                DetalleDocumentoCobranza detalleDoc = new DetalleDocumentoCobranza();

                // Asignar el documento de cobranza
                detalleDoc.setDocumentoCobranza(documentoCobranza);

                // Mapear datos desde el detalle de cotización
                detalleDoc.setCantidad(1); // Cada registro individual tiene cantidad = 1
                detalleDoc.setDescripcion(detalleCot.getDescripcion());
                detalleDoc.setPrecio(
                        detalleCot.getPrecioHistorico() != null ? detalleCot.getPrecioHistorico() : BigDecimal.ZERO);

                // Asignar producto si existe
                if (detalleCot.getProducto() != null) {
                    Producto producto = new Producto();
                    producto.setId(detalleCot.getProducto().getId());
                    detalleDoc.setProducto(producto);
                }

                // Guardar el detalle
                detalleDocumentoCobranzaRepository.save(detalleDoc);
            }
        }
    }

    // Implementación de métodos para gestión de carpetas

    @Override
    public List<DocumentoCobranzaResponseDTO> findByCarpeta(Integer carpetaId) {
        return mapToResponseList(documentoCobranzaRepository.findByCarpetaId(carpetaId));
    }

    @Override
    public List<DocumentoCobranzaResponseDTO> findSinCarpeta() {
        return mapToResponseList(documentoCobranzaRepository.findByCarpetaIsNull());
    }

    @Override
    @Transactional
    public DocumentoCobranzaResponseDTO updateCarpeta(Long id, Integer carpetaId) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + id));

        if (carpetaId != null) {
            Carpeta carpeta = carpetaRepository.findById(carpetaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId));
            documentoCobranza.setCarpeta(carpeta);
        } else {
            documentoCobranza.setCarpeta(null);
        }

        return documentoCobranzaMapper.toResponseDTO(documentoCobranzaRepository.save(documentoCobranza));
    }
}