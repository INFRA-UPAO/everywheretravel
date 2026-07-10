package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.dto.LiquidacionConDetallesResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionSimpleDTO;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.model.dto.PagoPaxResponseDTO;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.DetalleLiquidacion;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionSimpleDTO;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.DetalleLiquidacionRepository;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.repository.LiquidacionRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.DetalleCotizacionService;
import com.everywhere.backend.service.LiquidacionService;
import com.everywhere.backend.service.DetalleLiquidacionService;
import com.everywhere.backend.service.ObservacionLiquidacionService;
import com.everywhere.backend.service.PagoPaxService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.LiquidacionMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LiquidacionServiceImpl implements LiquidacionService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final LiquidacionRepository liquidacionRepository;
    private final LiquidacionMapper liquidacionMapper;
    private final DetalleLiquidacionService detalleLiquidacionService;
    private final DetalleLiquidacionRepository detalleLiquidacionRepository;
    private final DetalleCotizacionService detalleCotizacionService;
    private final CotizacionRepository cotizacionRepository;
    private final CarpetaRepository carpetaRepository;
    private final ObservacionLiquidacionService observacionLiquidacionService;
    private final PagoPaxService pagoPaxService;
    private final FormaPagoRepository formaPagoRepository;
    private final ProductoRepository productoRepository;

    @Override
    public List<LiquidacionResponseDTO> findAll() {
        return liquidacionRepository.findAll().stream().map(liquidacionMapper::toResponseDTO).toList();
    }

    @Override
    public LiquidacionResponseDTO findById(Integer id) {
        Liquidacion liquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));
        return liquidacionMapper.toResponseDTO(liquidacion);
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO update(Integer id, LiquidacionRequestDTO liquidacionRequestDTO) {
        if (!liquidacionRepository.existsById(id))
            throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + id);

        if (liquidacionRequestDTO.getCotizacionId() != null &&
                !cotizacionRepository.existsById(liquidacionRequestDTO.getCotizacionId()))
            throw new ResourceNotFoundException(
                    "Cotización no encontrada con ID: " + liquidacionRequestDTO.getCotizacionId());

        if (liquidacionRequestDTO.getProductoId() != null &&
                !productoRepository.existsById(liquidacionRequestDTO.getProductoId()))
            throw new ResourceNotFoundException(
                    "Producto no encontrado con ID: " + liquidacionRequestDTO.getProductoId());

        if (liquidacionRequestDTO.getFormaPagoId() != null &&
                !formaPagoRepository.existsById(liquidacionRequestDTO.getFormaPagoId()))
            throw new ResourceNotFoundException(
                    "Forma de pago no encontrada con ID: " + liquidacionRequestDTO.getFormaPagoId());

        if (liquidacionRequestDTO.getCarpetaId() != null &&
                !carpetaRepository.existsById(liquidacionRequestDTO.getCarpetaId()))
            throw new ResourceNotFoundException(
                    "Carpeta no encontrada con ID: " + liquidacionRequestDTO.getCarpetaId());

        Liquidacion liquidacion = liquidacionRepository.findById(id).get();
        liquidacionMapper.updateEntityFromRequest(liquidacion, liquidacionRequestDTO);

        if (liquidacionRequestDTO.getCotizacionId() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(liquidacionRequestDTO.getCotizacionId()).get();
            liquidacion.setCotizacion(cotizacion);
        }

        if (liquidacionRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(liquidacionRequestDTO.getProductoId()).get();
            liquidacion.setProducto(producto);
        }

        if (liquidacionRequestDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(liquidacionRequestDTO.getFormaPagoId()).get();
            liquidacion.setFormaPago(formaPago);
        }

        if (liquidacionRequestDTO.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(liquidacionRequestDTO.getCarpetaId()).get();
            liquidacion.setCarpeta(carpeta);
        }

        return liquidacionMapper.toResponseDTO(liquidacionRepository.save(liquidacion));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!liquidacionRepository.existsById(id))
            throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + id);
        liquidacionRepository.deleteById(id);
    }

    @Override
    public LiquidacionConDetallesResponseDTO findByIdWithDetalles(Integer id) {
        Liquidacion liquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));

        LiquidacionResponseDTO liquidacionResponseDTO = liquidacionMapper.toResponseDTO(liquidacion);

        List<DetalleLiquidacionResponseDTO> detalleLiquidacionResponseDTOs = detalleLiquidacionService
                .findByLiquidacionId(id);
        List<DetalleLiquidacionSimpleDTO> detalleLiquidacionSimpleDTOs = detalleLiquidacionResponseDTOs.stream()
                .map(this::convertirADetalleSimple).toList();

        List<ObservacionLiquidacionResponseDTO> observacionLiquidacionResponseDTOS = observacionLiquidacionService
                .findByLiquidacionId(id);
        List<ObservacionLiquidacionSimpleDTO> observacionLiquidacionSimpleDTOs = observacionLiquidacionResponseDTOS
                .stream().map(this::convertirAObservacionSimple).toList();

        LiquidacionConDetallesResponseDTO liquidacionConDetallesResponseDTO = new LiquidacionConDetallesResponseDTO();
        liquidacionConDetallesResponseDTO.setId(liquidacionResponseDTO.getId());
        liquidacionConDetallesResponseDTO.setNumero(liquidacionResponseDTO.getNumero());
        liquidacionConDetallesResponseDTO.setFechaCompra(liquidacionResponseDTO.getFechaCompra());
        liquidacionConDetallesResponseDTO.setDestino(liquidacionResponseDTO.getDestino());
        liquidacionConDetallesResponseDTO.setNumeroPasajeros(liquidacionResponseDTO.getNumeroPasajeros());
        liquidacionConDetallesResponseDTO.setCreado(liquidacionResponseDTO.getCreado());
        liquidacionConDetallesResponseDTO.setActualizado(liquidacionResponseDTO.getActualizado());
        liquidacionConDetallesResponseDTO.setProducto(liquidacionResponseDTO.getProducto());
        liquidacionConDetallesResponseDTO.setFormaPago(liquidacionResponseDTO.getFormaPago());
        liquidacionConDetallesResponseDTO.setDetalles(detalleLiquidacionSimpleDTOs);
        liquidacionConDetallesResponseDTO.setObservaciones(observacionLiquidacionSimpleDTOs);

        return liquidacionConDetallesResponseDTO;
    }

    @Override
    public ByteArrayInputStream generateExcel(Integer liquidacionId) {
        LiquidacionConDetallesResponseDTO liquidacion = findByIdWithDetalles(liquidacionId);
        List<PagoPaxResponseDTO> pagosPax = pagoPaxService.findByLiquidacionId(liquidacionId);

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);

            createResumenSheet(workbook, liquidacion, pagosPax, headerStyle, moneyStyle);
            createDetallesSheet(workbook, liquidacion.getDetalles(), headerStyle, moneyStyle);
            createObservacionesSheet(workbook, liquidacion.getObservaciones(), headerStyle, moneyStyle);
            createPagosPaxSheet(workbook, pagosPax, headerStyle, moneyStyle);

            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Error al generar el Excel de la liquidación", e);
        }
    }

    private void createResumenSheet(Workbook workbook, LiquidacionConDetallesResponseDTO liquidacion,
            List<PagoPaxResponseDTO> pagosPax, CellStyle headerStyle, CellStyle moneyStyle) {
        Sheet sheet = workbook.createSheet("Resumen");

        Row headerRow = sheet.createRow(0);
        Cell campoHeader = headerRow.createCell(0);
        campoHeader.setCellValue("Campo");
        campoHeader.setCellStyle(headerStyle);
        Cell valorHeader = headerRow.createCell(1);
        valorHeader.setCellValue("Valor");
        valorHeader.setCellStyle(headerStyle);

        int rowIndex = 1;
        addResumenValue(sheet, rowIndex++, "Número", toText(liquidacion.getNumero()));
        addResumenValue(sheet, rowIndex++, "Fecha Compra", formatDate(liquidacion.getFechaCompra()));
        addResumenValue(sheet, rowIndex++, "Destino", toText(liquidacion.getDestino()));
        addResumenValue(sheet, rowIndex++, "Número de Pasajeros", toText(liquidacion.getNumeroPasajeros()));
        addResumenValue(sheet, rowIndex++, "Producto", getLiquidacionProducto(liquidacion));
        addResumenValue(sheet, rowIndex++, "Forma de Pago", getLiquidacionFormaPago(liquidacion));
        addResumenValue(sheet, rowIndex++, "Creado", formatDateTime(liquidacion.getCreado()));
        addResumenValue(sheet, rowIndex++, "Actualizado", formatDateTime(liquidacion.getActualizado()));

        rowIndex++;

        addResumenMoneyValue(sheet, rowIndex++, "Total Costo Ticket",
                sumDetalles(liquidacion.getDetalles(), DetalleLiquidacionSimpleDTO::getCostoTicket), moneyStyle);
        addResumenMoneyValue(sheet, rowIndex++, "Total Cargo Servicio",
                sumDetalles(liquidacion.getDetalles(), DetalleLiquidacionSimpleDTO::getCargoServicio), moneyStyle);
        addResumenMoneyValue(sheet, rowIndex++, "Total Valor Venta",
                sumDetalles(liquidacion.getDetalles(), DetalleLiquidacionSimpleDTO::getValorVenta), moneyStyle);
        addResumenMoneyValue(sheet, rowIndex++, "Total Monto Descuento",
                sumDetalles(liquidacion.getDetalles(), DetalleLiquidacionSimpleDTO::getMontoDescuento), moneyStyle);
        addResumenMoneyValue(sheet, rowIndex++, "Total Pagos PAX USD",
                sumPagosPaxByMoneda(pagosPax, "USD"), moneyStyle);
        addResumenMoneyValue(sheet, rowIndex++, "Total Pagos PAX PEN",
                sumPagosPaxByMoneda(pagosPax, "PEN"), moneyStyle);
        addResumenValue(sheet, rowIndex, "Cantidad de Pagos PAX", toText(pagosPax == null ? 0 : pagosPax.size()));

        autoSizeColumns(sheet, 2);
    }

    private void createDetallesSheet(Workbook workbook, List<DetalleLiquidacionSimpleDTO> detalles, CellStyle headerStyle,
            CellStyle moneyStyle) {
        Sheet sheet = workbook.createSheet("Detalles");

        String[] headers = {
                "#",
                "Viajero",
                "Producto",
                "Proveedor",
                "Operador",
                "Ticket",
                "Doc. Cobro",
                "Costo Ticket",
                "Cargo Servicio",
                "Valor Venta",
                "Fee Emisión",
                "Doc. Fee",
                "Comisión",
                "Factura Compra",
                "Boleta Pasajero",
                "Monto Descuento",
                "Creado",
                "Actualizado"
        };

        createHeaderRow(sheet, headers, headerStyle);

        if (detalles == null || detalles.isEmpty()) {
            Row emptyRow = sheet.createRow(1);
            emptyRow.createCell(0).setCellValue("Sin detalles");
            autoSizeColumns(sheet, headers.length);
            return;
        }

        int rowIndex = 1;
        int nro = 1;
        for (DetalleLiquidacionSimpleDTO detalle : detalles) {
            Row row = sheet.createRow(rowIndex++);
            int col = 0;

            row.createCell(col++).setCellValue(nro++);
            row.createCell(col++).setCellValue(getViajeroNombre(detalle));
            row.createCell(col++).setCellValue(getProductoNombre(detalle));
            row.createCell(col++).setCellValue(detalle.getProveedor() != null ? toText(detalle.getProveedor().getNombre()) : "");
            row.createCell(col++).setCellValue(detalle.getOperador() != null ? toText(detalle.getOperador().getNombre()) : "");
            row.createCell(col++).setCellValue(toText(detalle.getTicket()));
            row.createCell(col++).setCellValue(toText(detalle.getDocumentoCobro()));
            setMoneyCell(row, col++, detalle.getCostoTicket(), moneyStyle);
            setMoneyCell(row, col++, detalle.getCargoServicio(), moneyStyle);
            setMoneyCell(row, col++, detalle.getValorVenta(), moneyStyle);
            row.createCell(col++).setCellValue(toText(detalle.getFeeEmision()));
            row.createCell(col++).setCellValue(toText(detalle.getDocumentoFee()));
            row.createCell(col++).setCellValue(toText(detalle.getComision()));
            row.createCell(col++).setCellValue(toText(detalle.getFacturaCompra()));
            row.createCell(col++).setCellValue(toText(detalle.getBoletaPasajero()));
            setMoneyCell(row, col++, detalle.getMontoDescuento(), moneyStyle);
            row.createCell(col++).setCellValue(formatDateTime(detalle.getCreado()));
            row.createCell(col).setCellValue(formatDateTime(detalle.getActualizado()));
        }

        Row totalRow = sheet.createRow(rowIndex);
        totalRow.createCell(0).setCellValue("TOTALES");
        setMoneyCell(totalRow, 7, sumDetalles(detalles, DetalleLiquidacionSimpleDTO::getCostoTicket), moneyStyle);
        setMoneyCell(totalRow, 8, sumDetalles(detalles, DetalleLiquidacionSimpleDTO::getCargoServicio), moneyStyle);
        setMoneyCell(totalRow, 9, sumDetalles(detalles, DetalleLiquidacionSimpleDTO::getValorVenta), moneyStyle);
        setMoneyCell(totalRow, 15, sumDetalles(detalles, DetalleLiquidacionSimpleDTO::getMontoDescuento), moneyStyle);

        autoSizeColumns(sheet, headers.length);
    }

    private void createObservacionesSheet(Workbook workbook,
            List<ObservacionLiquidacionSimpleDTO> observaciones,
            CellStyle headerStyle,
            CellStyle moneyStyle) {
        Sheet sheet = workbook.createSheet("Observaciones");
        String[] headers = {
                "#",
                "Descripción",
                "Creado",
                "Actualizado"
        };

        createHeaderRow(sheet, headers, headerStyle);

        if (observaciones == null || observaciones.isEmpty()) {
            Row emptyRow = sheet.createRow(1);
            emptyRow.createCell(0).setCellValue("Sin observaciones");
            autoSizeColumns(sheet, headers.length);
            return;
        }

        int rowIndex = 1;
        int nro = 1;
        for (ObservacionLiquidacionSimpleDTO observacion : observaciones) {
            Row row = sheet.createRow(rowIndex++);
            int col = 0;
            row.createCell(col++).setCellValue(nro++);
            row.createCell(col++).setCellValue(toText(observacion.getDescripcion()));
            row.createCell(col++).setCellValue(formatDateTime(observacion.getCreado()));
            row.createCell(col).setCellValue(formatDateTime(observacion.getActualizado()));
        }

        autoSizeColumns(sheet, headers.length);
    }

    private void createPagosPaxSheet(Workbook workbook,
            List<PagoPaxResponseDTO> pagosPax,
            CellStyle headerStyle,
            CellStyle moneyStyle) {
        Sheet sheet = workbook.createSheet("Pagos PAX");
        String[] headers = {
                "#",
                "Monto",
                "Moneda",
                "Detalle",
                "Forma de Pago",
                "Creado",
                "Actualizado"
        };

        createHeaderRow(sheet, headers, headerStyle);

        if (pagosPax == null || pagosPax.isEmpty()) {
            Row emptyRow = sheet.createRow(1);
            emptyRow.createCell(0).setCellValue("Sin pagos PAX");
            autoSizeColumns(sheet, headers.length);
            return;
        }

        int rowIndex = 1;
        int nro = 1;
        for (PagoPaxResponseDTO pagoPax : pagosPax) {
            Row row = sheet.createRow(rowIndex++);
            int col = 0;
            row.createCell(col++).setCellValue(nro++);
            setMoneyCell(row, col++, pagoPax.getMonto(), moneyStyle);
            row.createCell(col++).setCellValue(toText(pagoPax.getMoneda()));
            row.createCell(col++).setCellValue(toText(pagoPax.getDetalle()));
            row.createCell(col++).setCellValue(pagoPax.getFormaPago() != null ? toText(pagoPax.getFormaPago().getDescripcion()) : "");
            row.createCell(col++).setCellValue(formatDateTime(pagoPax.getCreado()));
            row.createCell(col).setCellValue(formatDateTime(pagoPax.getActualizado()));
        }

        autoSizeColumns(sheet, headers.length);
    }

    private void createHeaderRow(Sheet sheet, String[] headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void addResumenValue(Sheet sheet, int rowIndex, String label, String value) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private void addResumenMoneyValue(Sheet sheet, int rowIndex, String label, BigDecimal value, CellStyle moneyStyle) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        setMoneyCell(row, 1, value, moneyStyle);
    }

    private void setMoneyCell(Row row, int columnIndex, BigDecimal value, CellStyle moneyStyle) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value.doubleValue() : 0D);
        cell.setCellStyle(moneyStyle);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private BigDecimal sumDetalles(List<DetalleLiquidacionSimpleDTO> detalles,
            Function<DetalleLiquidacionSimpleDTO, BigDecimal> extractor) {
        if (detalles == null || detalles.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return detalles.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumPagosPaxByMoneda(List<PagoPaxResponseDTO> pagosPax, String moneda) {
        if (pagosPax == null || pagosPax.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return pagosPax.stream()
                .filter(pago -> pago.getMoneda() != null && pago.getMoneda().trim().toUpperCase(Locale.ROOT)
                        .equals(moneda.toUpperCase(Locale.ROOT)))
                .map(PagoPaxResponseDTO::getMonto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getLiquidacionProducto(LiquidacionConDetallesResponseDTO liquidacion) {
        if (liquidacion.getProducto() == null) {
            return "";
        }
        if (liquidacion.getProducto().getTipo() != null && !liquidacion.getProducto().getTipo().isBlank()) {
            return liquidacion.getProducto().getTipo();
        }
        return toText(liquidacion.getProducto().getDescripcion());
    }

    private String getLiquidacionFormaPago(LiquidacionConDetallesResponseDTO liquidacion) {
        if (liquidacion.getFormaPago() == null) {
            return "";
        }
        return toText(liquidacion.getFormaPago().getDescripcion());
    }

    private String getViajeroNombre(DetalleLiquidacionSimpleDTO detalle) {
        if (detalle.getViajero() == null || detalle.getViajero().getPersonaNatural() == null) {
            return "";
        }

        String nombres = toText(detalle.getViajero().getPersonaNatural().getNombres());
        String apellidoPaterno = toText(detalle.getViajero().getPersonaNatural().getApellidosPaterno());
        String apellidoMaterno = toText(detalle.getViajero().getPersonaNatural().getApellidosMaterno());

        return (nombres + " " + apellidoPaterno + " " + apellidoMaterno).trim().replaceAll("\\s+", " ");
    }

    private String getProductoNombre(DetalleLiquidacionSimpleDTO detalle) {
        if (detalle.getProducto() == null) {
            return "";
        }
        if (detalle.getProducto().getTipo() != null && !detalle.getProducto().getTipo().isBlank()) {
            return detalle.getProducto().getTipo();
        }
        return toText(detalle.getProducto().getDescripcion());
    }

    private String toText(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_TIME_FORMATTER);
    }

    private DetalleLiquidacionSimpleDTO convertirADetalleSimple(
            DetalleLiquidacionResponseDTO detalleLiquidacionResponseDTO) {
        DetalleLiquidacionSimpleDTO detalleLiquidacionSimpleDTO = new DetalleLiquidacionSimpleDTO();
        detalleLiquidacionSimpleDTO.setId(detalleLiquidacionResponseDTO.getId());
        detalleLiquidacionSimpleDTO.setTicket(detalleLiquidacionResponseDTO.getTicket());
        detalleLiquidacionSimpleDTO.setDocumentoCobro(detalleLiquidacionResponseDTO.getDocumentoCobro());
        detalleLiquidacionSimpleDTO.setCostoTicket(detalleLiquidacionResponseDTO.getCostoTicket());
        detalleLiquidacionSimpleDTO.setCargoServicio(detalleLiquidacionResponseDTO.getCargoServicio());
        detalleLiquidacionSimpleDTO.setValorVenta(detalleLiquidacionResponseDTO.getValorVenta());
        detalleLiquidacionSimpleDTO.setFeeEmision(detalleLiquidacionResponseDTO.getFeeEmision());
        detalleLiquidacionSimpleDTO.setDocumentoFee(detalleLiquidacionResponseDTO.getDocumentoFee());
        detalleLiquidacionSimpleDTO.setComision(detalleLiquidacionResponseDTO.getComision());
        detalleLiquidacionSimpleDTO.setFacturaCompra(detalleLiquidacionResponseDTO.getFacturaCompra());
        detalleLiquidacionSimpleDTO.setBoletaPasajero(detalleLiquidacionResponseDTO.getBoletaPasajero());
        detalleLiquidacionSimpleDTO.setMontoDescuento(detalleLiquidacionResponseDTO.getMontoDescuento());
        detalleLiquidacionSimpleDTO.setPagoPaxUSD(detalleLiquidacionResponseDTO.getPagoPaxUSD());
        detalleLiquidacionSimpleDTO.setPagoPaxPEN(detalleLiquidacionResponseDTO.getPagoPaxPEN());
        detalleLiquidacionSimpleDTO.setCreado(detalleLiquidacionResponseDTO.getCreado());
        detalleLiquidacionSimpleDTO.setActualizado(detalleLiquidacionResponseDTO.getActualizado());

        detalleLiquidacionSimpleDTO.setViajero(detalleLiquidacionResponseDTO.getViajero());
        detalleLiquidacionSimpleDTO.setProducto(detalleLiquidacionResponseDTO.getProducto());
        detalleLiquidacionSimpleDTO.setProveedor(detalleLiquidacionResponseDTO.getProveedor());
        detalleLiquidacionSimpleDTO.setOperador(detalleLiquidacionResponseDTO.getOperador());

        return detalleLiquidacionSimpleDTO;
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO create(LiquidacionRequestDTO liquidacionRequestDTO, Integer cotizacionId) {
        if (!cotizacionRepository.existsById(cotizacionId))
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + cotizacionId);

        if (liquidacionRequestDTO.getProductoId() != null &&
                !productoRepository.existsById(liquidacionRequestDTO.getProductoId()))
            throw new ResourceNotFoundException(
                    "Producto no encontrado con ID: " + liquidacionRequestDTO.getProductoId());

        if (liquidacionRequestDTO.getFormaPagoId() != null &&
                !formaPagoRepository.existsById(liquidacionRequestDTO.getFormaPagoId()))
            throw new ResourceNotFoundException(
                    "Forma de pago no encontrada con ID: " + liquidacionRequestDTO.getFormaPagoId());

        if (liquidacionRequestDTO.getCarpetaId() != null &&
                !carpetaRepository.existsById(liquidacionRequestDTO.getCarpetaId()))
            throw new ResourceNotFoundException(
                    "Carpeta no encontrada con ID: " + liquidacionRequestDTO.getCarpetaId());

        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId).get();

        Liquidacion liquidacion = liquidacionMapper.toEntity(liquidacionRequestDTO);
        liquidacion.setCotizacion(cotizacion);

        if (liquidacionRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(liquidacionRequestDTO.getProductoId()).get();
            liquidacion.setProducto(producto);
        }

        if (liquidacionRequestDTO.getFormaPagoId() != null) {
            FormaPago formaPago = formaPagoRepository.findById(liquidacionRequestDTO.getFormaPagoId()).get();
            liquidacion.setFormaPago(formaPago);
        }

        if (liquidacionRequestDTO.getCarpetaId() != null) {
            Carpeta carpeta = carpetaRepository.findById(liquidacionRequestDTO.getCarpetaId()).get();
            liquidacion.setCarpeta(carpeta);
        }

        liquidacion = liquidacionRepository.save(liquidacion); // Guardar la liquidación primero para obtener el ID
        crearDetallesDesdeCotizacion(liquidacion, cotizacionId);
        return liquidacionMapper.toResponseDTO(liquidacion);
    }

    /**
     * Método privado que crea los detalles de liquidación basándose en los detalles de la cotización.
     * Implementa la lógica de repartición: por cada detalle seleccionado de la cotización,
     * crea N detalles de liquidación donde N = cantidad del detalle de cotización.
     */
    private void crearDetallesDesdeCotizacion(Liquidacion liquidacion, Integer cotizacionId) {
        // Obtener todos los detalles de la cotizacion
        List<DetalleCotizacionResponseDto> detallesCotizacion = detalleCotizacionService
                .findByCotizacionId(cotizacionId);

        // Filtrar solo los detalles seleccionados
        List<DetalleCotizacionResponseDto> detallesSeleccionados = detallesCotizacion.stream()
                .filter(detalle -> detalle.getSeleccionado() != null && detalle.getSeleccionado())
                .toList();

        // Por cada detalle seleccionado, crear N detalles de liquidación (donde N = cantidad)
        for (DetalleCotizacionResponseDto detalleCot : detallesSeleccionados) {
            int cantidad = detalleCot.getCantidad() != null ? detalleCot.getCantidad() : 1;

            // Crear un detalle de liquidación por cada unidad de cantidad
            for (int i = 0; i < cantidad; i++) {
                DetalleLiquidacion detalleLiq = new DetalleLiquidacion();

                // Asignar la liquidación
                detalleLiq.setLiquidacion(liquidacion);

                // Mapear datos desde el detalle de cotización
                detalleLiq.setCostoTicket(
                        detalleCot.getPrecioHistorico() != null ? detalleCot.getPrecioHistorico() : BigDecimal.ZERO);
                detalleLiq.setCargoServicio(
                        detalleCot.getComision() != null ? detalleCot.getComision() : BigDecimal.ZERO);

                // Asignar producto y proveedor si existen
                if (detalleCot.getProducto() != null) {
                    detalleLiq.setProducto(detalleCot.getProducto());
                }
                if (detalleCot.getProveedor() != null) {
                    detalleLiq.setProveedor(detalleCot.getProveedor());
                }

                // Inicializar otros campos con valores por defecto (se llenarán después)
                detalleLiq.setTicket("");
                detalleLiq.setValorVenta(BigDecimal.ZERO);
                detalleLiq.setFacturaCompra("");
                detalleLiq.setBoletaPasajero("");
                detalleLiq.setMontoDescuento(BigDecimal.ZERO);
                detalleLiq.setPagoPaxUSD(BigDecimal.ZERO);
                detalleLiq.setPagoPaxPEN(BigDecimal.ZERO);
                // Viajero y Operador se quedan null para ser asignados después
                
                detalleLiquidacionRepository.save(detalleLiq);
            }
        }
    }

    private ObservacionLiquidacionSimpleDTO convertirAObservacionSimple(
            ObservacionLiquidacionResponseDTO observacionLiquidacionResponseDTO) {
        ObservacionLiquidacionSimpleDTO observacionLiquidacionSimpleDTO = new ObservacionLiquidacionSimpleDTO();
        observacionLiquidacionSimpleDTO.setId(observacionLiquidacionResponseDTO.getId());
        observacionLiquidacionSimpleDTO.setDescripcion(observacionLiquidacionResponseDTO.getDescripcion());
        observacionLiquidacionSimpleDTO.setValor(observacionLiquidacionResponseDTO.getValor());
        observacionLiquidacionSimpleDTO.setDocumento(observacionLiquidacionResponseDTO.getDocumento());
        observacionLiquidacionSimpleDTO.setNumeroDocumento(observacionLiquidacionResponseDTO.getNumeroDocumento());
        observacionLiquidacionSimpleDTO.setCreado(observacionLiquidacionResponseDTO.getCreado());
        observacionLiquidacionSimpleDTO.setActualizado(observacionLiquidacionResponseDTO.getActualizado());
        return observacionLiquidacionSimpleDTO;
    }

    // Implementación de métodos para gestión de carpetas

    @Override
    public List<LiquidacionResponseDTO> findByCarpeta(Integer carpetaId) {
        if (!carpetaRepository.existsById(carpetaId))
            throw new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId);

        return liquidacionRepository.findByCarpetaId(carpetaId).stream()
                .map(liquidacionMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<LiquidacionResponseDTO> findSinCarpeta() {
        return liquidacionRepository.findByCarpetaIsNull().stream()
                .map(liquidacionMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO updateCarpeta(Integer id, Integer carpetaId) {
        Liquidacion liquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + id));

        if (carpetaId != null) {
            // Asociar a una carpeta
            Carpeta carpeta = carpetaRepository.findById(carpetaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId));
            liquidacion.setCarpeta(carpeta);
        } else {
            // Desasociar de la carpeta
            liquidacion.setCarpeta(null);
        }

        return liquidacionMapper.toResponseDTO(liquidacionRepository.save(liquidacion));
    }
}