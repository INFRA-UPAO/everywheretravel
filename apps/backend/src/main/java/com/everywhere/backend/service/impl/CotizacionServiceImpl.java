package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.mapper.CotizacionMapper;
import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.*;
import com.everywhere.backend.service.CotizacionService;
import com.everywhere.backend.service.DetalleCotizacionService;
import com.everywhere.backend.service.HistorialCotizacionService;
import com.everywhere.backend.service.aws.S3DocumentService;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final CotizacionMapper cotizacionMapper;
    private final FormaPagoRepository formaPagoRepository;
    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final CounterRepository counterRepository;
    private final SucursalRepository sucursalRepository;
    private final CarpetaRepository carpetaRepository;
    private final PersonaRepository personasRepository;
    private final DetalleCotizacionService detalleCotizacionService;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final HistorialCotizacionService historialCotizacionService;
    private final S3DocumentService s3DocumentService;

    // Diccionario de tipos de productos
    private static final Map<String, String> DICCIONARIO_PRODUCTOS = Map.of(
            "TKT", "Pasajes aéreos",
            "HTL", "Hoteles",
            "TRS", "Traslados",
            "TUR", "Tours",
            "SEG", "Seguros",
            "OTR", "Otros servicios");

    private static final DateTimeFormatter DOCX_FECHA_EMISION_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy',' HH:mm:ss");

    @Override
    public CotizacionResponseDto create(CotizacionRequestDto cotizacionRequestDto, Integer personaId) {

        Cotizacion cotizacion = cotizacionMapper.toEntity(cotizacionRequestDto);
        cotizacion.setCodigoCotizacion(generateCodigoCotizacion());

        if (personaId == null)
            throw new DataIntegrityViolationException("PersonaId es obligatorio para crear una cotización");

        Personas persona = personasRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con id " + personaId));
        cotizacion.setPersonas(persona);

        if (cotizacionRequestDto.getCounterId() != null) {
            Counter counter = counterRepository.findById(cotizacionRequestDto.getCounterId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Counter no encontrado con id " + cotizacionRequestDto.getCounterId()));
            cotizacion.setCounter(counter);
        }

        if (cotizacionRequestDto.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(cotizacionRequestDto.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Forma de pago no encontrada con id " + cotizacionRequestDto.getFormaPagoId()));
            cotizacion.setFormaPago(formaPago);
        }

        if (cotizacionRequestDto.getEstadoCotizacionId() != null) {
            EstadoCotizacion estado = estadoCotizacionRepository.findById(cotizacionRequestDto.getEstadoCotizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado de cotización no encontrado con id "
                            + cotizacionRequestDto.getEstadoCotizacionId()));
            cotizacion.setEstadoCotizacion(estado);
        }

        if (cotizacionRequestDto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(cotizacionRequestDto.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada con id " + cotizacionRequestDto.getSucursalId()));
            cotizacion.setSucursal(sucursal);
        }

        if (cotizacionRequestDto.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(cotizacionRequestDto.getCarpetaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Carpeta no encontrada con id " + cotizacionRequestDto.getCarpetaId()));
            cotizacion.setCarpeta(carpeta);
        }

        Cotizacion savedCotizacion = cotizacionRepository.save(cotizacion);
        registrarHistorialSiTieneEstado(savedCotizacion,
            "Registro inicial del estado de la cotización");

        return cotizacionMapper.toResponse(savedCotizacion);
    }

    @Override
    public CotizacionResponseDto findById(Integer id) {
        return cotizacionRepository.findById(id).map(cotizacionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
    }

    @Override
    public List<CotizacionResponseDto> findAll() {
        return mapToResponseList(cotizacionRepository.findAll());
    }

    @Override
    public CotizacionResponseDto update(Integer id, CotizacionRequestDto cotizacionRequestDto) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        Integer estadoAnteriorId = cotizacion.getEstadoCotizacion() != null
            ? cotizacion.getEstadoCotizacion().getId()
            : null;

        cotizacionMapper.updateEntityFromRequest(cotizacion, cotizacionRequestDto);

        if (cotizacionRequestDto.getCounterId() != null) {
            Counter counter = counterRepository.findById(cotizacionRequestDto.getCounterId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Counter no encontrado con id " + cotizacionRequestDto.getCounterId()));
            cotizacion.setCounter(counter);
        }

        if (cotizacionRequestDto.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(cotizacionRequestDto.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Forma de pago no encontrada con id " + cotizacionRequestDto.getFormaPagoId()));
            cotizacion.setFormaPago(formaPago);
        }

        if (cotizacionRequestDto.getEstadoCotizacionId() != null) {
            EstadoCotizacion estado = estadoCotizacionRepository.findById(cotizacionRequestDto.getEstadoCotizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado de cotización no encontrado con id "
                            + cotizacionRequestDto.getEstadoCotizacionId()));
            cotizacion.setEstadoCotizacion(estado);
        }

        if (cotizacionRequestDto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(cotizacionRequestDto.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada con id " + cotizacionRequestDto.getSucursalId()));
            cotizacion.setSucursal(sucursal);
        }

        if (cotizacionRequestDto.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(cotizacionRequestDto.getCarpetaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Carpeta no encontrada con id " + cotizacionRequestDto.getCarpetaId()));
            cotizacion.setCarpeta(carpeta);
        }

        Cotizacion updatedCotizacion = cotizacionRepository.save(cotizacion);
        Integer estadoActualId = updatedCotizacion.getEstadoCotizacion() != null
            ? updatedCotizacion.getEstadoCotizacion().getId()
            : null;

        if (!Objects.equals(estadoAnteriorId, estadoActualId)) {
            registrarHistorialSiTieneEstado(updatedCotizacion,
                "Actualización registrado automáticamente");
        }

        return cotizacionMapper.toResponse(updatedCotizacion);
    }

    @Override
    public void delete(Integer id) {
        if (!cotizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + id);

        try {
            cotizacionRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(
                "No se puede eliminar esta cotización porque tiene dependencias asociadas (detalles de cotización, documentos u otros registros). Elimine primero las dependencias relacionadas.");
        } catch (Exception e) {
            throw new BadRequestException("Error al eliminar la cotización: " + e.getMessage());
        }
    }

    private String generateCodigoCotizacion() {
        Integer maxId = cotizacionRepository.findMaxId();
        int next = (maxId != null ? maxId + 1 : 1);
        return String.format("COT-%03d", next);
    }

    @Override
    public CotizacionConDetallesResponseDTO findByIdWithDetalles(Integer id) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        CotizacionResponseDto cotizacionDTO = cotizacionMapper.toResponse(cotizacion);
        List<DetalleCotizacionResponseDto> detallesCompletos = detalleCotizacionService.findByCotizacionId(id);
        List<DetalleCotizacionSimpleDTO> detallesSimples = detallesCompletos.stream()
                .map(cotizacionMapper::toDetalleSimple).toList();
        return cotizacionMapper.toResponseWithDetalles(cotizacionDTO, detallesSimples);
    }

    @Override
    public List<CotizacionResponseDto> findCotizacionesSinLiquidacion() {
        return mapToResponseList(cotizacionRepository.findCotizacionesSinLiquidacion());
    }

    private List<CotizacionResponseDto> mapToResponseList(List<Cotizacion> cotizaciones) {
        return cotizaciones.stream().map(cotizacionMapper::toResponse).toList();
    }

    private void registrarHistorialSiTieneEstado(Cotizacion cotizacion, String observacion) {
        if (cotizacion.getEstadoCotizacion() == null) {
            return;
        }

        historialCotizacionService.registrarCambioEstado(
                cotizacion.getId(),
                cotizacion.getEstadoCotizacion().getId(),
                observacion);
    }

    private Map<String, Object> agruparDetallesPorTipoProducto(List<DetalleCotizacionSimpleDTO> detalles) {
        Map<String, Object> resultado = new LinkedHashMap<>();

        // Separar productos FIJOS (categoría id=1) de OPCIONES (otras categorías)
        List<DetalleCotizacionSimpleDTO> detallesFijos = detalles.stream()
                .filter(detalle -> detalle.getCategoria() != null && detalle.getCategoria().getId() == 1)
                .toList();

        List<DetalleCotizacionSimpleDTO> detallesOpciones = detalles.stream()
                .filter(detalle -> detalle.getCategoria() != null && detalle.getCategoria().getId() != 1)
                .toList();

        // Agrupar detalles FIJOS por tipo de producto
        Map<String, List<DetalleCotizacionSimpleDTO>> detallesFijosPorTipo = detallesFijos.stream()
                .filter(detalle -> detalle.getProducto() != null && detalle.getProducto().getTipo() != null)
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProducto().getTipo(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        // Agrupar detalles OPCIONES por nombre de categoría
        Map<String, List<DetalleCotizacionSimpleDTO>> detallesOpcionesPorCategoria = detallesOpciones.stream()
                .filter(detalle -> detalle.getCategoria() != null && detalle.getCategoria().getNombre() != null)
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getCategoria().getNombre(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        // Crear estructura para grupos de productos FIJOS
        List<Map<String, Object>> gruposProductosFijos = new ArrayList<>();
        BigDecimal importeTotalFijos = BigDecimal.ZERO;

        for (Map.Entry<String, List<DetalleCotizacionSimpleDTO>> entry : detallesFijosPorTipo.entrySet()) {
            String tipoProducto = entry.getKey();
            List<DetalleCotizacionSimpleDTO> detallesDelTipo = entry.getValue();

            BigDecimal importeTotalGrupo = detallesDelTipo.stream()
                    .map(this::calcularImporteDetalle)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            importeTotalFijos = importeTotalFijos.add(importeTotalGrupo);

            Map<String, Object> grupo = new LinkedHashMap<>();
            grupo.put("codigo", tipoProducto);
            grupo.put("nombre", DICCIONARIO_PRODUCTOS.getOrDefault(tipoProducto, tipoProducto));
            grupo.put("detalles", detallesDelTipo);
            grupo.put("importeTotal", importeTotalGrupo);

            gruposProductosFijos.add(grupo);
        }

        // Crear estructura para grupos de OPCIONES por categoría
        List<Map<String, Object>> gruposOpciones = new ArrayList<>();
        String[] colores = { "FFF9C4", "FFECB3", "FFE0B2", "FFCCBC", "D7CCC8", "F5F5F5", "CFD8DC", "B2DFDB" }; // Colores suaves
        int colorIndex = 0;

        for (Map.Entry<String, List<DetalleCotizacionSimpleDTO>> entry : detallesOpcionesPorCategoria.entrySet()) {
            String nombreCategoria = entry.getKey();
            List<DetalleCotizacionSimpleDTO> detallesDeCategoria = entry.getValue();

            BigDecimal importeTotalCategoria = detallesDeCategoria.stream()
                    .map(this::calcularImporteDetalle)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> grupo = new LinkedHashMap<>();
            grupo.put("nombreCategoria", nombreCategoria);
            grupo.put("detalles", detallesDeCategoria);
            grupo.put("importeTotal", importeTotalCategoria);
            grupo.put("color", colores[colorIndex % colores.length]);
            colorIndex++;

            gruposOpciones.add(grupo);
        }

        BigDecimal importeGrupoMasEconomico = gruposOpciones.stream()
                .map(grupo -> (BigDecimal) grupo.get("importeTotal"))
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        resultado.put("detallesFijos", detallesFijos);
        resultado.put("gruposProductosFijos", gruposProductosFijos);
        resultado.put("gruposOpciones", gruposOpciones);
        resultado.put("importeTotalGeneral", importeTotalFijos.add(importeGrupoMasEconomico));

        return resultado;
    }

    @Override
    public ByteArrayInputStream generateDocx(Integer cotizacionId) {
        // Obtener la cotización con todos sus detalles
        CotizacionConDetallesResponseDTO cotizacion = findByIdWithDetalles(cotizacionId);

        if (cotizacion == null) {
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + cotizacionId);
        }

        // Detectar si hay productos de tipo TKT (vuelos)
        boolean tieneVuelos = cotizacion.getDetalles().stream()
                .anyMatch(detalle -> detalle.getProducto() != null && "TKT".equals(detalle.getProducto().getTipo()));

        // Agrupar detalles por tipo de producto
        Map<String, Object> datosAgrupados = agruparDetallesPorTipoProducto(cotizacion.getDetalles());

        try {
            // Cargar la plantilla DOCX existente
            String templatePath = "/static/documents/PLANTILLA.docx";
            InputStream templateStream = getClass().getResourceAsStream(templatePath);

            if (templateStream == null) {
                throw new ResourceNotFoundException("No se encontró la plantilla en: " + templatePath);
            }

            // Abrir la plantilla como documento
            XWPFDocument document = new XWPFDocument(templateStream);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // 1. Encabezado superior
            XWPFParagraph headingParagraph = document.createParagraph();
            headingParagraph.setAlignment(ParagraphAlignment.CENTER);
            headingParagraph.setSpacingBefore(300);
            headingParagraph.setSpacingAfter(20);

            XWPFRun headingRun = headingParagraph.createRun();
            headingRun.setText("COTIZACIÓN");
            headingRun.setBold(true);
            headingRun.setFontSize(17);

            // 2. Título de la cotización (nombre)
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            titleParagraph.setSpacingBefore(0);
            titleParagraph.setSpacingAfter(80);

            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(resolveDocxTitle(cotizacion));
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            XWPFParagraph durationParagraph = document.createParagraph();
            durationParagraph.setAlignment(ParagraphAlignment.CENTER);
            durationParagraph.setSpacingAfter(60);

            XWPFRun durationRun = durationParagraph.createRun();
            durationRun.setText(resolveDuracionViaje(cotizacion));
            durationRun.setBold(true);
            durationRun.setFontSize(12);

            XWPFParagraph routeParagraph = document.createParagraph();
            routeParagraph.setAlignment(ParagraphAlignment.CENTER);
            routeParagraph.setSpacingAfter(80);

            XWPFRun routeRun = routeParagraph.createRun();
            routeRun.setText("Destino: " + resolveRutaCotizacion(cotizacion));
            routeRun.setFontSize(11);

            // 2. Información general de la cotización
            addCotizacionInfo(document, cotizacion);

            // 3. Tabla completa de productos FIJOS
            @SuppressWarnings("unchecked")
            List<DetalleCotizacionSimpleDTO> detallesFijos = (List<DetalleCotizacionSimpleDTO>) datosAgrupados
                    .get("detallesFijos");
            addTodosLosDetallesTable(document, detallesFijos);

            // 4. Tablas agrupadas de productos FIJOS por tipo
            // addDetallesAgrupadosPorTipo(document, datosAgrupados);

            // 5. Sección de OPCIONES (categorías no FIJAS)
            addOpcionesPorCategoriaHotel(document, datosAgrupados);

            // 6. NUEVA: Tabla de Tarifas de Vuelo (solo si hay productos TKT)
            if (tieneVuelos) {
                addTablaTarifasVuelo(document);
            }

            // 7. Sección de Importe a Pagar (total de cotización más económica)
            int totalPersonas = cotizacion.getCantAdultos() + cotizacion.getCantNinos();

            // Debug: Si totalPersonas es 0, usar 1 para evitar división por cero
            if (totalPersonas == 0) {
                totalPersonas = 1; // Al menos 1 persona por defecto
            }

            addImporteAPagarConPersonas(document, datosAgrupados, totalPersonas);

            // 8. Condiciones de Tarifa
            addCondicionesTarifa(document);

            // 9. Política de Privacidad
            addPoliticaPrivacidad(document);

            document.write(out);
            document.close();
            templateStream.close();

            // Subir a S3 en segundo plano (no bloquea la respuesta al usuario)
            byte[] docxBytes = out.toByteArray();
            try {
                String s3Key = String.format("generated/cotizaciones/Cotizacion_%d_%d.docx",
                        cotizacionId, System.currentTimeMillis());
                s3DocumentService.uploadDocument(s3Key, docxBytes,
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            } catch (Exception s3Ex) {
                // No bloquear la descarga directa si falla la subida a S3
                log.warn("No se pudo subir el DOCX a S3 para cotizacion {}: {}", cotizacionId, s3Ex.getMessage());
            }

            return new ByteArrayInputStream(docxBytes);

        } catch (Exception e) {
            throw new ResourceNotFoundException("Error al generar el DOCX de la cotizacion: " + e.getMessage());
        }
    }

    private void addCotizacionInfo(XWPFDocument document, CotizacionConDetallesResponseDTO cotizacion) {
        XWPFParagraph infoParagraph = document.createParagraph();
        infoParagraph.setSpacingBefore(0);
        infoParagraph.setSpacingAfter(120);

        XWPFRun infoRun = infoParagraph.createRun();
        infoRun.setText("Fecha Emisión: " + formatFechaEmision(cotizacion.getFechaEmision()));
        infoRun.addBreak();
        infoRun.setText("Fecha Vencimiento: " + formatFechaEmision(cotizacion.getFechaVencimiento()));
        infoRun.addBreak();

        // Obtener nombre completo del cliente
        String clienteInfo = "N/A";
        if (cotizacion.getPersonas() != null) {
            try {
                PersonaNatural personaNatural = personaNaturalRepository
                        .findByPersonasId(cotizacion.getPersonas().getId()).orElse(null);
                if (personaNatural != null) {
                    StringBuilder nombreCompleto = new StringBuilder();
                    if (personaNatural.getNombres() != null) {
                        nombreCompleto.append(personaNatural.getNombres());
                    }
                    if (personaNatural.getApellidosPaterno() != null) {
                        if (nombreCompleto.length() > 0)
                            nombreCompleto.append(" ");
                        nombreCompleto.append(personaNatural.getApellidosPaterno());
                    }
                    if (personaNatural.getApellidosMaterno() != null) {
                        if (nombreCompleto.length() > 0)
                            nombreCompleto.append(" ");
                        nombreCompleto.append(personaNatural.getApellidosMaterno());
                    }
                    clienteInfo = nombreCompleto.length() > 0 ? nombreCompleto.toString()
                            : cotizacion.getPersonas().getEmail();
                } else {
                    clienteInfo = cotizacion.getPersonas().getEmail() != null ? cotizacion.getPersonas().getEmail()
                            : "ID: " + cotizacion.getPersonas().getId();
                }
            } catch (Exception e) {
                clienteInfo = cotizacion.getPersonas().getEmail() != null ? cotizacion.getPersonas().getEmail()
                        : "ID: " + cotizacion.getPersonas().getId();
            }
        }
        infoRun.setText("Cliente: " + clienteInfo);
        infoRun.addBreak();
        infoRun.setText("Adultos: " + cotizacion.getCantAdultos() + " | Niños: " + cotizacion.getCantNinos());
        infoRun.setFontSize(11);
    }

    private String resolveDocxTitle(CotizacionConDetallesResponseDTO cotizacion) {
        if (cotizacion.getNombreCotizacion() != null && !cotizacion.getNombreCotizacion().trim().isEmpty()) {
            return cotizacion.getNombreCotizacion().trim();
        }

        if (cotizacion.getCodigoCotizacion() != null && !cotizacion.getCodigoCotizacion().trim().isEmpty()) {
            return "Cotización " + cotizacion.getCodigoCotizacion().trim();
        }

        return "Cotización";
    }

    private String resolveDuracionViaje(CotizacionConDetallesResponseDTO cotizacion) {
        LocalDate fechaSalida = cotizacion.getFechaSalida();
        LocalDate fechaRegreso = cotizacion.getFechaRegreso();

        if (fechaSalida == null || fechaRegreso == null) {
            return "0 DIAS / 0 NOCHES";
        }

        long noches = ChronoUnit.DAYS.between(fechaSalida, fechaRegreso);
        if (noches < 0) {
            noches = 0;
        }

        long dias = noches + 1;
        return dias + " DIAS / " + noches + " NOCHES";
    }

    private String resolveRutaCotizacion(CotizacionConDetallesResponseDTO cotizacion) {
        return cotizacion.getOrigenDestino() != null && !cotizacion.getOrigenDestino().trim().isEmpty()
                ? cotizacion.getOrigenDestino().trim()
                : "N/A";
    }

    private String formatFechaEmision(LocalDateTime fechaEmision) {
        if (fechaEmision == null) {
            return "N/A";
        }
        return fechaEmision.format(DOCX_FECHA_EMISION_FORMATTER);
    }

    private BigDecimal calcularImporteDetalle(DetalleCotizacionSimpleDTO detalle) {
        BigDecimal precio = detalle.getPrecioHistorico() != null ? detalle.getPrecioHistorico() : BigDecimal.ZERO;
        BigDecimal comision = detalle.getComision() != null ? detalle.getComision() : BigDecimal.ZERO;
        Integer cantidad = detalle.getCantidad() != null ? detalle.getCantidad() : 1;
        return precio.add(comision).multiply(BigDecimal.valueOf(cantidad));
    }

    private void addTodosLosDetallesTable(XWPFDocument document, List<DetalleCotizacionSimpleDTO> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        // Título "Productos"
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setSpacingBefore(200);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("PRODUCTOS");
        titleRun.setBold(true);
        titleRun.setFontSize(14);

        XWPFTable table = document.createTable();
        table.setWidth("100%");

        // Encabezados de la tabla
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.addNewTableCell().setText("Tipo");
        headerRow.getCell(0).setText("Cantidad");
        headerRow.addNewTableCell().setText("Operador");
        //Las columnas de precio unitario y total se omiten en esta tabla general por reglas de negocio
        //headerRow.addNewTableCell().setText("Precio Unit.");
        //headerRow.addNewTableCell().setText("Total");

        // Agregar detalles
        for (DetalleCotizacionSimpleDTO detalle : detalles) {
            XWPFTableRow row = table.createRow();

            // Cantidad
            row.getCell(0).setText(detalle.getCantidad() != null ? detalle.getCantidad().toString() : "0");

            // Tipo de producto
            String tipo = "N/A";
            if (detalle.getProducto() != null && detalle.getProducto().getTipo() != null) {
                tipo = detalle.getProducto().getTipo();
            }
            row.getCell(1).setText(tipo);

            // Operador
            String operador = "N/A";
            if (detalle.getOperador() != null && detalle.getOperador().getNombre() != null) {
                operador = detalle.getOperador().getNombre();
            }
            row.getCell(2).setText(operador);

            //row.getCell(3)
            //        .setText(detalle.getPrecioHistorico() != null ? String.format("%.2f", detalle.getPrecioHistorico().add(detalle.getComision()))
            //                : "0.00");

            // Calcular total: cantidad * precio
            //BigDecimal total = BigDecimal.ZERO;
            //if (detalle.getCantidad() != null && detalle.getPrecioHistorico() != null && detalle.getComision() != null) {
            //    total = detalle.getPrecioHistorico().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            //}
            //row.getCell(4).setText(String.format("%.2f", total));
        }

        XWPFParagraph spaceParagraph = document.createParagraph();
        spaceParagraph.setSpacingAfter(200);
    }

    // Esta función se mantiene pero se comenta para no mostrar tablas agrupadas por tipo de producto, solo la tabla general. Se puede reactivar si se desea mostrar ambas secciones (general y agrupada).
    /*@SuppressWarnings("unchecked")
    private void addDetallesAgrupadosPorTipo(XWPFDocument document, Map<String, Object> datosAgrupados) {
        List<Map<String, Object>> gruposProductos = (List<Map<String, Object>>) datosAgrupados
                .get("gruposProductosFijos");

        if (gruposProductos == null || gruposProductos.isEmpty()) {
            return;
        }

        for (Map<String, Object> grupo : gruposProductos) {
            String codigo = (String) grupo.get("codigo");
            String nombre = (String) grupo.get("nombre");
            List<DetalleCotizacionSimpleDTO> detalles = (List<DetalleCotizacionSimpleDTO>) grupo.get("detalles");
            BigDecimal importeTotal = (BigDecimal) grupo.get("importeTotal");

            // Subtítulo del grupo (ej: "TKT - Pasajes aéreos")
            XWPFParagraph subtitleParagraph = document.createParagraph();
            subtitleParagraph.setSpacingBefore(300);
            XWPFRun subtitleRun = subtitleParagraph.createRun();
            subtitleRun.setText(codigo + " - " + nombre);
            subtitleRun.setBold(true);
            subtitleRun.setFontSize(13);
            subtitleRun.setColor("1F2937");

            // Tabla de detalles del grupo
            XWPFTable table = document.createTable();
            table.setWidth("100%");

            // Encabezados
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("Descripción");
            headerRow.addNewTableCell().setText("Operador");
            headerRow.addNewTableCell().setText("Cantidad");
            headerRow.addNewTableCell().setText("Precio Unit.");
            headerRow.addNewTableCell().setText("Total");

            // Agregar detalles del grupo
            for (DetalleCotizacionSimpleDTO detalle : detalles) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(detalle.getDescripcion() != null ? detalle.getDescripcion() : "");

                // Operador
                String operador = "N/A";
                if (detalle.getOperador() != null && detalle.getOperador().getNombre() != null) {
                    operador = detalle.getOperador().getNombre();
                }
                row.getCell(1).setText(operador);

                row.getCell(2).setText(detalle.getCantidad() != null ? detalle.getCantidad().toString() : "0");
                row.getCell(3)
                        .setText(detalle.getPrecioHistorico() != null
                                ? String.format("%.2f", detalle.getPrecioHistorico())
                                : "0.00");

                BigDecimal total = BigDecimal.ZERO;
                if (detalle.getCantidad() != null && detalle.getPrecioHistorico() != null) {
                    total = detalle.getPrecioHistorico().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                }
                row.getCell(4).setText(String.format("%.2f", total));
            }

            // Cuadro con importe total del grupo
            XWPFParagraph totalParagraph = document.createParagraph();
            totalParagraph.setSpacingBefore(100);
            totalParagraph.setSpacingAfter(100);
            XWPFRun totalRun = totalParagraph.createRun();
            totalRun.setText("Importe Total " + nombre + ": USD " + String.format("%.2f", importeTotal));
            totalRun.setBold(true);
            totalRun.setFontSize(12);
            totalRun.setColor("DC2626");

            XWPFParagraph spaceParagraph = document.createParagraph();
            spaceParagraph.setSpacingAfter(100);
        }
    }
    */
    @SuppressWarnings("unchecked")
    private void addOpcionesPorCategoriaHotel(XWPFDocument document, Map<String, Object> datosAgrupados) {
        List<Map<String, Object>> gruposOpciones = (List<Map<String, Object>>) datosAgrupados.get("gruposOpciones");

        if (gruposOpciones == null || gruposOpciones.isEmpty()) {
            return;
        }

        // Título principal "OPCIONES"
        XWPFParagraph mainTitleParagraph = document.createParagraph();
        mainTitleParagraph.setSpacingBefore(400);
        mainTitleParagraph.setSpacingAfter(200);
        XWPFRun mainTitleRun = mainTitleParagraph.createRun();
        mainTitleRun.setText("HOTELERÍA");
        mainTitleRun.setBold(true);
        mainTitleRun.setFontSize(16);

        for (Map<String, Object> grupo : gruposOpciones) {
            String nombreCategoria = (String) grupo.get("nombreCategoria");
            List<DetalleCotizacionSimpleDTO> detalles = (List<DetalleCotizacionSimpleDTO>) grupo.get("detalles");
            String color = (String) grupo.get("color");

            // Subtítulo de la categoría
            XWPFParagraph subtitleParagraph = document.createParagraph();
            subtitleParagraph.setSpacingBefore(300);
            XWPFRun subtitleRun = subtitleParagraph.createRun();
            subtitleRun.setText(nombreCategoria);
            subtitleRun.setBold(true);
            subtitleRun.setFontSize(13);
            subtitleRun.setColor("1F2937");

            // Tabla de detalles con color de fondo
            XWPFTable table = document.createTable();
            table.setWidth("100%");

            // Encabezados
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("Descripción");
            headerRow.getCell(0).setColor(color);
            XWPFTableCell proveedorCell = headerRow.addNewTableCell();
            proveedorCell.setText("Operador");
            proveedorCell.setColor(color);
            XWPFTableCell cantidadCell = headerRow.addNewTableCell();
            cantidadCell.setText("Cantidad");
            cantidadCell.setColor(color);
            XWPFTableCell precioCell = headerRow.addNewTableCell();
            precioCell.setText("Precio Unit.");
            precioCell.setColor(color);
            XWPFTableCell totalCell = headerRow.addNewTableCell();
            totalCell.setText("Total");
            totalCell.setColor(color);

            // Agregar detalles de la categoría
            for (DetalleCotizacionSimpleDTO detalle : detalles) {
                XWPFTableRow row = table.createRow();

                // Aplicar color de fondo a todas las celdas
                row.getCell(0).setText(detalle.getDescripcion() != null ? detalle.getDescripcion() : "");
                row.getCell(0).setColor(color);

                String operador = "N/A";
                if (detalle.getOperador() != null && detalle.getOperador().getNombre() != null) {
                    operador = detalle.getOperador().getNombre();
                }
                row.getCell(1).setText(operador);
                row.getCell(1).setColor(color);

                row.getCell(2).setText(detalle.getCantidad() != null ? detalle.getCantidad().toString() : "0");
                row.getCell(2).setColor(color);

                row.getCell(3)
                        .setText(detalle.getPrecioHistorico() != null
                        ? "USD " + String.format("%.2f", detalle.getPrecioHistorico().add(detalle.getComision()))
                        : "USD 0.00");
                row.getCell(3).setColor(color);

                BigDecimal total = BigDecimal.ZERO;
                if (detalle.getCantidad() != null && detalle.getPrecioHistorico() != null && detalle.getComision() != null) {
                    total = detalle.getPrecioHistorico().add(detalle.getComision()).multiply(BigDecimal.valueOf(detalle.getCantidad()));
                }
                row.getCell(4).setText("USD " + String.format("%.2f", total));
                row.getCell(4).setColor(color);
            }

            XWPFParagraph spaceParagraph = document.createParagraph();
            spaceParagraph.setSpacingAfter(100);
        }
    }
    
    private void addImporteAPagarConPersonas(XWPFDocument document, Map<String, Object> datosAgrupados,
            int cantidadPersonas) {
        // Título "Pagos"
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setSpacingBefore(300);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("Pagos");
        titleRun.setBold(true);
        titleRun.setFontSize(14);

        // Obtener el importe total general
        BigDecimal importeTotalGeneral = (BigDecimal) datosAgrupados.get("importeTotalGeneral");
        double total = importeTotalGeneral != null ? importeTotalGeneral.doubleValue() : 0.0;

        // Calcular precio por persona - usar BigDecimal para mayor precisión
        BigDecimal precioPorPersona = BigDecimal.ZERO;
        if (cantidadPersonas > 0 && importeTotalGeneral != null) {
            precioPorPersona = importeTotalGeneral.divide(BigDecimal.valueOf(cantidadPersonas), 2,
                    java.math.RoundingMode.HALF_UP);
        }

        // Crear tabla para "Importe a Pagar" centrada y ancho completo
        XWPFTable table = document.createTable(2, 1);
        table.setWidth("100%"); // Ancho completo

        // Centrar la tabla
        table.getCTTbl().addNewTblPr().addNewJc()
                .setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STJcTable.CENTER);

        // Fila superior roja: IMPORTE A PAGAR
        XWPFTableRow row1 = table.getRow(0);
        XWPFTableCell cell1 = row1.getCell(0);
        cell1.setColor("DC2626"); // Rojo
        XWPFParagraph p1 = cell1.getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER); // Centrar texto
        XWPFRun r1 = p1.createRun();
        r1.setText("IMPORTE A PAGAR");
        r1.setBold(true);
        r1.setColor("FFFFFF"); // Blanco
        r1.setFontSize(14);

        // Fila inferior: Detalles
        XWPFTableRow row2 = table.getRow(1);
        XWPFTableCell cell2 = row2.getCell(0);
        XWPFParagraph p2 = cell2.getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER); // Centrar texto
        XWPFRun rTotal = p2.createRun();
        // Primero mostrar el precio total por la cantidad de personas
        if (cantidadPersonas > 0) {
            rTotal.setText("Precio por " + cantidadPersonas + " persona(s): USD " + String.format("%.2f", total));
        } else {
            rTotal.setText("Precio por cantidad de personas: N/A");
        }
        rTotal.setBold(true);
        rTotal.setFontSize(16);
        rTotal.addBreak();

        // Debajo mostrar el precio por persona
        XWPFRun rPersona = p2.createRun();
        if (cantidadPersonas > 0) {
            rPersona.setText("Precio por persona: USD " + String.format("%.2f", precioPorPersona));
        } else {
            rPersona.setText("Precio por persona: N/A (cantidad de personas no especificada)");
        }
        rPersona.setBold(false);
        rPersona.setFontSize(12);

        XWPFParagraph spaceParagraph = document.createParagraph();
        spaceParagraph.setSpacingAfter(200);
    }

    private void addCondicionesTarifa(XWPFDocument document) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setSpacingBefore(200);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("CONDICIONES DE TARIFA:");
        titleRun.setBold(true);
        titleRun.setFontSize(12);

        String[] condiciones = {
                "Tarifa \"NO REEMBOLSABLE\", no permite devoluciones.",
                "Tarifas sujetas a cambios acorde a disponibilidad de la misma.",
                "Puede realizar cambios antes de la fecha de viaje inicial contratada hasta 1 día antes de ese viaje. Después de ese plazo, hasta 6 horas antes del vuelo, los cambios deben realizarse directamente con la línea aérea. No se permiten cambios después de este período.",
                "Cualquier modificación está sujeta al pago de una penalidad de $ por persona, así como un cargo de reemisión $35 y diferencias de tarifa, si las hay."
        };

        for (String condicion : condiciones) {
            XWPFParagraph condicionParagraph = document.createParagraph();
            condicionParagraph.setIndentationLeft(400);
            XWPFRun condicionRun = condicionParagraph.createRun();
            condicionRun.setText("• " + condicion);
            condicionRun.setFontSize(10);
        }

        XWPFParagraph spaceParagraph = document.createParagraph();
        spaceParagraph.setSpacingAfter(200);
    }

    private void addTablaTarifasVuelo(XWPFDocument document) {
        // Título
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setSpacingBefore(400);
        titleParagraph.setSpacingAfter(200);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("Información sobre el vuelo");
        titleRun.setBold(true);
        titleRun.setFontSize(14);

        // Crear tabla 4 columnas x 5 filas
        XWPFTable table = document.createTable(5, 4);
        table.setWidth("100%");

        // FILA 1: Headers (Vacío, BASIC, LIGHT, PLUS)
        XWPFTableRow row1 = table.getRow(0);
        row1.getCell(0).setText("");
        row1.getCell(0).setColor("FFFFFF"); // Blanco

        row1.getCell(1).setText("BASIC");
        row1.getCell(1).setColor("87CEEB"); // Azul celeste
        row1.getCell(1).getParagraphs().get(0).getRuns().get(0).setBold(true);
        row1.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

        row1.getCell(2).setText("LIGHT");
        row1.getCell(2).setColor("87CEEB"); // Azul celeste
        row1.getCell(2).getParagraphs().get(0).getRuns().get(0).setBold(true);
        row1.getCell(2).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

        row1.getCell(3).setText("PLUS");
        row1.getCell(3).setColor("87CEEB"); // Azul celeste
        row1.getCell(3).getParagraphs().get(0).getRuns().get(0).setBold(true);
        row1.getCell(3).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

        // FILA 2: Precios (Adulto con fondo amarillo)
        XWPFTableRow row2 = table.getRow(1);
        row2.getCell(0).setText("Adulto");
        row2.getCell(0).setColor("FFFFFF");

        row2.getCell(1).setText("$ 0.00");
        row2.getCell(1).setColor("FFFF99"); // Amarillo
        row2.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

        row2.getCell(2).setText("$ 0.00");
        row2.getCell(2).setColor("FFFF99"); // Amarillo
        row2.getCell(2).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

        row2.getCell(3).setText("$ 0.00");
        row2.getCell(3).setColor("FFFF99"); // Amarillo
        row2.getCell(3).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

        // FILA 3: Características principales
        XWPFTableRow row3 = table.getRow(2);
        row3.getCell(0).setText("");
        row3.getCell(0).setColor("FFFFFF");

        // BASIC (Azul)
        XWPFParagraph p1 = row3.getCell(1).getParagraphs().get(0);
        p1.createRun().setText("Basic");
        p1.getRuns().get(0).setBold(true);
        p1.getRuns().get(0).setColor("0000FF");
        p1.createRun().addBreak();
        p1.createRun().setText("• Bolso o mochila.");
        p1.getRuns().get(0).setBold(true);
        p1.createRun().addBreak();
        p1.createRun().setText("• Cambio con cargo + diferencia de precio.");
        p1.createRun().addBreak();
        p1.createRun().setText("• No aplican beneficios por categorías de socios.");
        row3.getCell(1).setColor("FFFFFF");

        // LIGHT (Verde)
        XWPFParagraph p2 = row3.getCell(2).getParagraphs().get(0);
        p2.createRun().setText("Light");
        p2.getRuns().get(0).setBold(true);
        p2.getRuns().get(0).setColor("008000");
        p2.createRun().addBreak();
        p2.createRun().setText("• Bolso o mochila.");
        p2.createRun().addBreak();
        p2.createRun().setText("• Maleta pequeña 12 kg.");
        p2.createRun().addBreak();
        p2.createRun().setText("• Cambio con cargo + diferencia de precio.");
        p2.createRun().addBreak();
        p2.createRun().setText("• Postulación a UPG con tramos.");
        row3.getCell(2).setColor("FFFFFF");

        // FULL (Magenta)
        XWPFParagraph p3 = row3.getCell(3).getParagraphs().get(0);
        p3.createRun().setText("Full");
        p3.getRuns().get(0).setBold(true);
        p3.getRuns().get(0).setColor("FF00FF");
        p3.createRun().addBreak();
        p3.createRun().setText("• Bolso o mochila.");
        p3.createRun().addBreak();
        p3.createRun().setText("• Maleta pequeña 12 kg.");
        p3.createRun().addBreak();
        p3.createRun().setText("• 1 equipaje de bodega 23 kg.");
        p3.createRun().addBreak();
        p3.createRun().setText("• Cambio sin cargo + diferencia de precio.");
        p3.createRun().addBreak();
        p3.createRun().setText("• Devolución antes de la salida del primer vuelo.");
        p3.createRun().addBreak();
        p3.createRun().setText("• Selección de asiento Estándar.");
        p3.createRun().addBreak();
        p3.createRun().setText("• Postulación a UPG con tramos.");
        row3.getCell(3).setColor("FFFFFF");

        // FILA 4: Esta tarifa incluye
        XWPFTableRow row4 = table.getRow(3);
        row4.getCell(0).setText("");
        row4.getCell(0).setColor("FFFFFF");

        XWPFParagraph p4_1 = row4.getCell(1).getParagraphs().get(0);
        p4_1.createRun().setText("Esta tarifa incluye:");
        p4_1.getRuns().get(0).setBold(true);
        p4_1.createRun().addBreak();
        XWPFRun r4_1 = p4_1.createRun();
        r4_1.setText("Bolso o mochila ");
        r4_1.setBold(true);
        p4_1.createRun().setText("Puede ser una cartera, un bolso para laptop o un bolso para bebé.");
        row4.getCell(1).setColor("FFFFFF");

        XWPFParagraph p4_2 = row4.getCell(2).getParagraphs().get(0);
        p4_2.createRun().setText("Esta tarifa incluye:");
        p4_2.getRuns().get(0).setBold(true);
        p4_2.createRun().addBreak();
        XWPFRun r4_2a = p4_2.createRun();
        r4_2a.setText("Bolso o mochila ");
        r4_2a.setBold(true);
        p4_2.createRun().setText("Puede ser una cartera, un bolso para laptop o un bolso para bebé.");
        p4_2.createRun().addBreak();
        p4_2.createRun().addBreak();
        XWPFRun r4_2b = p4_2.createRun();
        r4_2b.setText("Maleta pequeña ");
        r4_2b.setBold(true);
        p4_2.createRun().setText("Equipaje con un peso máximo de 12 kg.");
        row4.getCell(2).setColor("FFFFFF");

        XWPFParagraph p4_3 = row4.getCell(3).getParagraphs().get(0);
        p4_3.createRun().setText("Esta tarifa incluye:");
        p4_3.getRuns().get(0).setBold(true);
        p4_3.createRun().addBreak();
        XWPFRun r4_3a = p4_3.createRun();
        r4_3a.setText("Bolso o mochila ");
        r4_3a.setBold(true);
        p4_3.createRun().setText("Puede ser una cartera, un bolso para laptop o un bolso para bebé.");
        p4_3.createRun().addBreak();
        p4_3.createRun().addBreak();
        XWPFRun r4_3b = p4_3.createRun();
        r4_3b.setText("Maleta pequeña ");
        r4_3b.setBold(true);
        p4_3.createRun().setText("Equipaje con un peso máximo de 12 kg.");
        p4_3.createRun().addBreak();
        p4_3.createRun().addBreak();
        XWPFRun r4_3c = p4_3.createRun();
        r4_3c.setText("1 equipajes de bodega ");
        r4_3c.setBold(true);
        p4_3.createRun().setText("Equipajes de bodega con un peso máximo de 23 kg cada uno.");
        p4_3.createRun().addBreak();
        p4_3.createRun().addBreak();
        XWPFRun r4_3d = p4_3.createRun();
        r4_3d.setText("Selección de asiento ");
        r4_3d.setBold(true);
        p4_3.createRun().setText("Asegura el lugar en el que quieres viajar.");
        row4.getCell(3).setColor("FFFFFF");

        // FILA 5: Extras incluidos
        XWPFTableRow row5 = table.getRow(4);
        row5.getCell(0).setText("");
        row5.getCell(0).setColor("FFFFFF");

        // Columna 1 - BASIC
        XWPFParagraph p5_1 = row5.getCell(1).getParagraphs().get(0);
        p5_1.createRun().setText("Extras incluidos:");
        p5_1.getRuns().get(0).setBold(true);
        p5_1.createRun().addBreak();
        p5_1.createRun().setText("Beneficios exclusivos de esta tarifa, no disponibles por separado");
        p5_1.createRun().addBreak();
        p5_1.createRun().setText("• Cambios");
        p5_1.createRun().addBreak();
        p5_1.createRun().setText("Rutas nacionales");
        p5_1.createRun().addBreak();
        p5_1.createRun().setText(
                "Se permiten cambios con cargo adicional antes de la hora del vuelo, más la diferencia de precio (en caso que aplique). Después de la hora del vuelo, no se pueden realizar cambios.");
        row5.getCell(1).setColor("FFFFFF");

        // Columna 2 - LIGHT
        XWPFParagraph p5_2 = row5.getCell(2).getParagraphs().get(0);
        p5_2.createRun().setText("Extras incluidos:");
        p5_2.getRuns().get(0).setBold(true);
        p5_2.createRun().addBreak();
        p5_2.createRun().setText("Beneficios exclusivos de esta tarifa, no disponibles por separado");
        p5_2.createRun().addBreak();
        p5_2.createRun().setText("• Cambios");
        p5_2.createRun().addBreak();
        p5_2.createRun().setText("Rutas nacionales (excepto en Brasil)");
        p5_2.createRun().addBreak();
        p5_2.createRun().setText(
                "Se permiten cambios con cargo adicional antes de la hora del vuelo, más la diferencia de precio (en caso que aplique). Después de la hora del vuelo, no se pueden realizar cambios.");
        row5.getCell(2).setColor("FFFFFF");

        // Columna 3 - PLUS
        XWPFParagraph p5_3 = row5.getCell(3).getParagraphs().get(0);
        p5_3.createRun().setText("Extras incluidos:");
        p5_3.getRuns().get(0).setBold(true);
        p5_3.createRun().addBreak();
        p5_3.createRun().setText("Beneficios exclusivos de esta tarifa, no disponibles por separado");
        p5_3.createRun().addBreak();
        p5_3.createRun().setText("• Cambios");
        p5_3.createRun().addBreak();
        p5_3.createRun().setText("Rutas nacionales (excepto en Brasil)");
        p5_3.createRun().addBreak();
        p5_3.createRun().setText(
                "Se permiten cambios sin cargo adicional antes de la hora del vuelo, más la diferencia de precio (en caso que aplique). Después de la hora del vuelo, los cambios tienen un cargo adicional.");
        row5.getCell(3).setColor("FFFFFF");

        XWPFParagraph spaceParagraph = document.createParagraph();
        spaceParagraph.setSpacingAfter(300);
    }

    private void addPoliticaPrivacidad(XWPFDocument document) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setSpacingBefore(300);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("POLÍTICA DE PRIVACIDAD");
        titleRun.setBold(true);
        titleRun.setFontSize(12);

        String[] parrafos = {
                "EVERYWHERE TRAVEL S.A.C. te informa sobre su Política de Privacidad respecto al tratamiento y protección de los datos de carácter personal en los usuarios y clientes que puedan ser recabados por la navegación o contratación de servicios a través de cualquier medio digital o del sitio Web https://everywhereviajes.com/.",
                "En este sentido, el Titular garantiza el cumplimiento de la normativa vigente en materia de protección de datos personales, reflejada en la Ley Orgánica 3/2018, de 5 de diciembre, de Protección de Datos Personales y de Garantía de Derechos Digitales (LOPD GDD). Cumple también con el Reglamento (UE) 2016/679 del Parlamento Europeo y del Consejo de 27 de abril de 2016 relativo a la protección de las personas físicas (RGPD).",
                "El uso de sitio Web y él envió de la foto del DNI o Pasaporte implica la aceptación de esta Política de Privacidad, así como las condiciones incluidas en el Aviso Legal."
        };

        for (String parrafo : parrafos) {
            XWPFParagraph p = document.createParagraph();
            p.setSpacingAfter(100);
            XWPFRun r = p.createRun();
            r.setText(parrafo);
            r.setFontSize(9);
        }

        // Términos y Condiciones
        XWPFParagraph terminosTitleParagraph = document.createParagraph();
        terminosTitleParagraph.setSpacingBefore(100);
        XWPFRun terminosTitleRun = terminosTitleParagraph.createRun();
        terminosTitleRun
                .setText("Términos y Condiciones de uso, el usuario y/o cliente reconoce que cumple lo siguiente:");
        terminosTitleRun.setBold(true);
        terminosTitleRun.setFontSize(10);

        String[] terminos = {
                "Es mayor de edad, conforme a la Ley peruana vigente. Si es menor de edad, por favor abstenerse de utilizar la herramienta.",
                "Es hábil en el idioma español.",
                "Se encuentra en pleno uso de sus facultades mentales y no adolece de vicio que afecte sus razonamiento, entendimiento y manifestación de voluntad. Por lo tanto, tiene plena capacidad civil, de acuerdo con la Ley peruana vigente.",
                "Ha leído íntegramente los Términos y Condiciones de uso.",
                "Actúa y declara únicamente por sí mismo y no en representación de terceros ni de menores de edad conforme a la Ley peruana vigente."
        };

        for (int i = 0; i < terminos.length; i++) {
            XWPFParagraph terminoParagraph = document.createParagraph();
            terminoParagraph.setIndentationLeft(400);
            XWPFRun terminoRun = terminoParagraph.createRun();
            terminoRun.setText((i + 1) + ". " + terminos[i]);
            terminoRun.setFontSize(9);
        }
    }

    // Implementación de métodos para gestión de carpetas

    @Override
    public List<CotizacionResponseDto> findByCarpeta(Integer carpetaId) {
        if (!carpetaRepository.existsById(carpetaId))
            throw new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId);

        return mapToResponseList(cotizacionRepository.findByCarpetaId(carpetaId));
    }

    @Override
    public List<CotizacionResponseDto> findSinCarpeta() {
        return mapToResponseList(cotizacionRepository.findByCarpetaIsNull());
    }

    @Override
    public CotizacionResponseDto updateCarpeta(Integer id, Integer carpetaId) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotizacion no encontrada con ID: " + id));

        if (carpetaId != null) { // Asociar a una carpeta
            Carpeta carpeta = carpetaRepository.findById(carpetaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId));
            cotizacion.setCarpeta(carpeta);
        } else {
            cotizacion.setCarpeta(null);
        }
        return cotizacionMapper.toResponse(cotizacionRepository.save(cotizacion));
    }
}