package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboUpdateDTO;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ReciboMapper;
import com.everywhere.backend.model.entity.Recibo;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.SucursalRepository;
import com.everywhere.backend.model.entity.DetalleRecibo;
import com.everywhere.backend.model.entity.DetalleDocumento;
import com.everywhere.backend.repository.DetalleReciboRepository;
import com.everywhere.backend.repository.DetalleDocumentoRepository;
import com.everywhere.backend.repository.ReciboRepository;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.repository.NaturalJuridicoRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.security.SecurityContextHelper;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DetalleCotizacionService;
import com.everywhere.backend.service.ReciboService;
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
public class ReciboServiceImpl implements ReciboService {

    private final CotizacionService cotizacionService;
    private final ReciboRepository reciboRepository;
    private final DetalleReciboRepository detalleReciboRepository;
    private final DetalleDocumentoRepository detalleDocumentoRepository;
    private final ReciboMapper reciboMapper;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final SucursalRepository sucursalRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final NaturalJuridicoRepository naturalJuridicoRepository;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final DetalleCotizacionService detalleCotizacionService;
    private final ProductoRepository productoRepository;
    private final CarpetaRepository carpetaRepository;
    private final com.everywhere.backend.util.pdf.ReciboPdfGenerator reciboPdfGenerator;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public ReciboResponseDTO createRecibo(Integer cotizacionId, Integer personaJuridicaId, Integer sucursalId) {
        if (cotizacionId == null)
            throw new IllegalArgumentException("El ID de la cotización no puede ser nulo");

        if (reciboRepository.findByCotizacionId(cotizacionId).isPresent())
            throw new DataIntegrityViolationException(
                    "Ya existe un recibo para la cotización ID: " + cotizacionId);

        CotizacionConDetallesResponseDTO cotizacion = cotizacionService.findByIdWithDetalles(cotizacionId);

        String[] serieCorrelativo = generateNextDocumentNumber();
        Recibo recibo = reciboMapper.fromCotizacion(cotizacion, serieCorrelativo[0],
                Integer.parseInt(serieCorrelativo[1]));

        // Validar y setear PersonaJuridica si fue proporcionada
        if (personaJuridicaId != null) {
            PersonaJuridica personaJuridica = personaJuridicaRepository.findById(personaJuridicaId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Persona jurídica no encontrada con ID: " + personaJuridicaId));

            // Validar que la PersonaJuridica esté asociada a la PersonaNatural de la
            // cotización
            if (cotizacion.getPersonas() != null) {
                Integer personaId = cotizacion.getPersonas().getId();
                PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId)
                        .orElse(null);

                if (personaNatural != null) {
                    boolean relacionExiste = naturalJuridicoRepository
                            .findByPersonaNaturalIdAndPersonaJuridicaId(personaNatural.getId(), personaJuridicaId)
                            .isPresent();

                    if (!relacionExiste) {
                        throw new IllegalArgumentException("La persona jurídica ID " + personaJuridicaId +
                                " no está asociada a la persona natural de la cotización");
                    }
                }
            }

            recibo.setPersonaJuridica(personaJuridica);
        }

        // Validar y setear Sucursal si fue proporcionada
        if (sucursalId != null) {
            Sucursal sucursal = sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));
            recibo.setSucursal(sucursal);
        }

        recibo = reciboRepository.save(recibo);

        // Crear detalles desde cotización
        crearDetallesDesdeCotizacion(recibo, cotizacionId);

        return reciboMapper.toResponseDTO(recibo);
    }

    @Override
    public ByteArrayInputStream generatePdf(Integer reciboId) {
        Recibo recibo = reciboRepository.findByIdWithRelations(reciboId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recibo no encontrado con ID: " + reciboId));

        reciboRepository.findByIdWithDetalles(reciboId)
                .ifPresent(r -> recibo.setDetalleRecibo(r.getDetalleRecibo()));

        ReciboResponseDTO reciboResponseDTO = reciboMapper.toResponseDTO(recibo);
        String userName = getAuthenticatedUserName();
        return reciboPdfGenerator.generatePdf(reciboResponseDTO, userName);
    }

    @Override
    public ReciboResponseDTO findById(Integer id) {
        Recibo recibo = reciboRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado con ID: " + id));
        return reciboMapper.toResponseDTO(recibo);
    }

    @Override
    public ReciboResponseDTO findBySerieAndCorrelativo(String serie, Integer correlativo) {
        Recibo recibo = reciboRepository.findBySerieAndCorrelativo(serie, correlativo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recibo no encontrado con serie: " + serie + " y correlativo: " + correlativo));
        return reciboMapper.toResponseDTO(recibo);
    }

    @Override
    public List<ReciboResponseDTO> findAll() {
        return mapToResponseList(reciboRepository.findAllForListing());
    }

    @Override
    public ReciboResponseDTO findByCotizacionId(Integer cotizacionId) {
        Recibo recibo = reciboRepository.findByCotizacionId(cotizacionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recibo no encontrado para cotización ID: " + cotizacionId));
        return reciboMapper.toResponseDTO(recibo);
    }

    @Override
    @Transactional
    public ReciboResponseDTO patchRecibo(Integer id, ReciboUpdateDTO reciboUpdateDTO) {
        if (!reciboRepository.existsById(id))
            throw new ResourceNotFoundException("Recibo no encontrado con ID: " + id);

        if (reciboUpdateDTO.getDetalleDocumentoId() != null &&
                !detalleDocumentoRepository.existsById(reciboUpdateDTO.getDetalleDocumentoId()))
            throw new ResourceNotFoundException(
                    "Detalle de documento no encontrado con ID: " + reciboUpdateDTO.getDetalleDocumentoId());

        Recibo recibo = reciboRepository.findById(id).get();
        reciboMapper.updateEntityFromUpdateDTO(recibo, reciboUpdateDTO);

        // Lógica mutuamente excluyente: DetalleDocumento XOR PersonaJuridica
        if (reciboUpdateDTO.getDetalleDocumentoId() != null) {
            DetalleDocumento detalleDocumento = detalleDocumentoRepository
                    .findById(reciboUpdateDTO.getDetalleDocumentoId()).get();
            recibo.setDetalleDocumento(detalleDocumento);
            recibo.setPersonaJuridica(null);
        } else if (reciboUpdateDTO.getPersonaJuridicaId() != null) {
            PersonaJuridica personaJuridica = personaJuridicaRepository
                    .findById(reciboUpdateDTO.getPersonaJuridicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: "
                            + reciboUpdateDTO.getPersonaJuridicaId()));

            if (recibo.getPersona() != null) {
                Integer personaId = recibo.getPersona().getId();
                PersonaNatural personaNatural = personaNaturalRepository.findByPersonasId(personaId)
                        .orElse(null);

                if (personaNatural != null) {
                    boolean relacionExiste = naturalJuridicoRepository
                            .findByPersonaNaturalIdAndPersonaJuridicaId(personaNatural.getId(),
                                    reciboUpdateDTO.getPersonaJuridicaId())
                            .isPresent();

                    if (!relacionExiste) {
                        throw new IllegalArgumentException(
                                "La persona jurídica ID " + reciboUpdateDTO.getPersonaJuridicaId() +
                                        " no está asociada a la persona natural del recibo");
                    }
                }
            }

            recibo.setPersonaJuridica(personaJuridica);
            recibo.setDetalleDocumento(null);
        }

        // Validar y actualizar Sucursal
        if (reciboUpdateDTO.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(reciboUpdateDTO.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada con ID: " + reciboUpdateDTO.getSucursalId()));
            recibo.setSucursal(sucursal);
        }

        // Validar y actualizar FormaPago
        if (reciboUpdateDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(reciboUpdateDTO.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Forma de pago no encontrada con ID: " + reciboUpdateDTO.getFormaPagoId()));
            recibo.setFormaPago(formaPago);
        }

        return reciboMapper.toResponseDTO(reciboRepository.save(recibo));
    }

    // ========== MÉTODOS PRIVADOS ==========
    private String[] generateNextDocumentNumber() {
        Optional<Recibo> lastReciboOpt = reciboRepository.findTopByOrderByIdDesc();

        if (lastReciboOpt.isPresent()) {
            Recibo lastRecibo = lastReciboOpt.get();
            String lastSerie = lastRecibo.getSerie();
            Integer lastCorrelativo = lastRecibo.getCorrelativo();

            if (lastSerie != null && lastCorrelativo != null) {
                // Incrementar correlativo
                int nextCorrelativo = lastCorrelativo + 1;
                return new String[] { lastSerie, String.valueOf(nextCorrelativo) };
            }
        }

        // Primer recibo: serie R01, correlativo 1
        return new String[] { "R01", "1" };
    }

    @Transactional
    private void crearDetallesDesdeCotizacion(Recibo recibo, Integer cotizacionId) {
        List<DetalleCotizacionResponseDto> detallesCotizacion = detalleCotizacionService
                .findByCotizacionId(cotizacionId);

        for (DetalleCotizacionResponseDto detalleCotizacion : detallesCotizacion) {
            DetalleRecibo detalleRecibo = new DetalleRecibo();
            detalleRecibo.setRecibo(recibo);
            detalleRecibo.setCantidad(detalleCotizacion.getCantidad() != null ? detalleCotizacion.getCantidad() : 0);
            detalleRecibo.setDescripcion(detalleCotizacion.getDescripcion());
            detalleRecibo.setPrecio(
                    detalleCotizacion.getPrecioHistorico() != null ? detalleCotizacion.getPrecioHistorico()
                            : BigDecimal.ZERO);

            if (detalleCotizacion.getProducto() != null && detalleCotizacion.getProducto().getId() > 0) {
                Producto producto = productoRepository.findById(detalleCotizacion.getProducto().getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Producto no encontrado con ID: " + detalleCotizacion.getProducto().getId()));
                detalleRecibo.setProducto(producto);
            }

            detalleReciboRepository.save(detalleRecibo);
        }
    }

    private List<ReciboResponseDTO> mapToResponseList(List<Recibo> recibos) {
        return recibos.stream().map(reciboMapper::toResponseDTO).toList();
    }

    private String getAuthenticatedUserName() {
        try {
            return securityContextHelper.getCurrentUser().getNombre();
        } catch (Exception e) {
            return "Usuario desconocido";
        }
    }

    // Implementación de métodos para gestión de carpetas

    @Override
    public List<ReciboResponseDTO> findByCarpeta(Integer carpetaId) {
        return mapToResponseList(reciboRepository.findByCarpetaId(carpetaId));
    }

    @Override
    public List<ReciboResponseDTO> findSinCarpeta() {
        return mapToResponseList(reciboRepository.findByCarpetaIsNull());
    }

    @Override
    @Transactional
    public ReciboResponseDTO updateCarpeta(Integer id, Integer carpetaId) {
        Recibo recibo = reciboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado con ID: " + id));

        if (carpetaId != null) {
            Carpeta carpeta = carpetaRepository.findById(carpetaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId));
            recibo.setCarpeta(carpeta);
        } else {
            recibo.setCarpeta(null);
        }

        return reciboMapper.toResponseDTO(reciboRepository.save(recibo));
    }
}
