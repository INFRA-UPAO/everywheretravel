package com.everywhere.backend.util.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

/**
 * Clase base abstracta para generadores de PDF con estructura común
 * Documentos que comparten formato: Documento de Cobranza, Recibo
 */
public abstract class PdfGenerator<T, D> {

        protected final NumberToTextConverter numberToTextConverter;

        protected PdfGenerator(NumberToTextConverter numberToTextConverter) {
                this.numberToTextConverter = numberToTextConverter;
        }

        /**
         * Método template para generar PDF
         */
        public ByteArrayInputStream generatePdf(T documentoDTO, String userName) {
                if (documentoDTO == null)
                        throw new ResourceNotFoundException("No se encontró el documento para generar el PDF");

                try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
                        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                        Document document = new Document(pdfDocument);

                        addCompanyHeader(document, documentoDTO);
                        addSummaryTable(document, documentoDTO);

                        List<D> detalles = getDetalles(documentoDTO);
                        if (detalles != null && !detalles.isEmpty())
                                addServicesTable(document, documentoDTO, userName);

                        addObservationsBox(document, documentoDTO);
                        addPageFooter(pdfDocument);

                        document.close();
                        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new ResourceNotFoundException("Error al generar el PDF: " + e.getMessage());
                }
        }

        // ========== MÉTODOS ABSTRACTOS (deben ser implementados por las subclases)
        // ==========

        /**
         * Retorna el título del documento (ej: "DOCUMENTO DE COBRANZA" o "RECIBO")
         */
        protected abstract String getDocumentTitle();

        /**
         * Retorna el texto del pie de página (ej: "Representación Impresa de DOCUMENTO
         * DE COBRANZA")
         */
        protected abstract String getFooterText();

        /**
         * Obtiene la lista de detalles del documento
         */
        protected abstract List<D> getDetalles(T documentoDTO);

        /**
         * Obtiene datos específicos del documento para el header
         */
        protected abstract String getNumeroDocumento(T documentoDTO);

        protected abstract String getFechaEmision(T documentoDTO);

        protected abstract String getClienteNombre(T documentoDTO);

        protected abstract String getClienteDocumento(T documentoDTO);

        protected abstract String getTipoDocumentoCliente(T documentoDTO);

        protected abstract String getSucursalDescripcion(T documentoDTO);

        protected abstract String getMoneda(T documentoDTO);

        protected abstract String getFileVenta(T documentoDTO);

        protected abstract String getFormaPagoDescripcion(T documentoDTO);

        protected abstract String getObservaciones(T documentoDTO);

        protected abstract BigDecimal getCostoEnvio(T documentoDTO);

        protected abstract Integer getCantidad(D detalle);

        protected abstract String getProductoDescripcion(D detalle);

        protected abstract String getDescripcionDetalle(D detalle);

        protected abstract BigDecimal getPrecio(D detalle);

        protected abstract Long getDetalleId(D detalle);

        // ========== MÉTODOS OPCIONALES (pueden ser sobrescritos por subclases)
        // ==========

        /**
         * Indica si se debe mostrar la fila de costo de envío en el PDF.
         * Por defecto retorna true. Sobrescribir en subclases para ocultar.
         */
        protected boolean showCostoEnvio() {
                return true;
        }

        /**
         * Indica si se debe mostrar el disclaimer "NO VÁLIDO PARA CRÉDITO FISCAL" en el
         * PDF.
         * Por defecto retorna true. Sobrescribir en subclases para ocultar.
         */
        protected boolean showDisclaimer() {
                return true;
        }

        /**
         * Obtiene la fecha de vencimiento del documento (opcional).
         * Por defecto retorna null. Sobrescribir en subclases que tengan fecha de
         * vencimiento.
         * 
         * @param documentoDTO el documento
         * @return String con la fecha de vencimiento formateada o null si no aplica
         */
        protected String getFechaVencimiento(T documentoDTO) {
                return null;
        }

        /**
         * Formatea un número decimal con separadores de miles (comas)
         * 
         * @param value el valor a formatear
         * @return String con el número formateado (ej: "1,234.56")
         */
        private String formatearNumeroConSeparadorMiles(BigDecimal value) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                symbols.setDecimalSeparator('.');

                DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
                return formatter.format(value);
        }

        // ========== MÉTODOS COMUNES (implementación compartida) ==========

        protected void addCompanyHeader(Document document, T documentoDTO) {
                Table headerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
                Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setWidth(UnitValue.createPercentValue(60));

                // Logo
                Table logoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
                                .setPadding(10).setHeight(60);

                try {
                        String logoPath = "/static/images/everyLogo.png";
                        java.io.InputStream logoStream = getClass().getResourceAsStream(logoPath);
                        byte[] logoBytes = logoStream.readAllBytes();
                        Image logoImage = new Image(ImageDataFactory.create(logoBytes));
                        logoImage.setWidth(150);
                        logoImage.setHeight(50);
                        logoCell.add(logoImage);
                        logoStream.close();
                } catch (Exception e) {
                        throw new ResourceNotFoundException("Error al cargar el logo para el PDF");
                }

                logoTable.addCell(logoCell);
                leftCell.add(logoTable);
                leftCell.add(new Paragraph("\n").setMarginTop(7));

                // Información de la empresa
                Table companyInfoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Cell companyInfoCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8);
                Table innerCompanyTable = new Table(1).setWidth(UnitValue.createPercentValue(100));

                innerCompanyTable.addCell(new Cell()
                                .add(new Paragraph("EVERYWHERE TRAVEL SAC").setBold().setFontSize(11))
                                .setBorder(Border.NO_BORDER).setPadding(1).setTextAlignment(TextAlignment.LEFT));
                innerCompanyTable.addCell(new Cell()
                                .add(new Paragraph("MZ.J' LTE.10 URB.SOLILUZ, TRUJILLO, PERU").setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1).setTextAlignment(TextAlignment.LEFT));
                innerCompanyTable.addCell(new Cell().add(new Paragraph("Teléfono: 044 729-728").setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1).setTextAlignment(TextAlignment.LEFT));
                innerCompanyTable.addCell(new Cell()
                                .add(new Paragraph("Celular: +51 944 493 851 / 947 755 582").setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1).setTextAlignment(TextAlignment.LEFT));

                companyInfoCell.add(innerCompanyTable);
                companyInfoTable.addCell(companyInfoCell).setBorder(Border.NO_BORDER);
                leftCell.add(companyInfoTable);
                leftCell.add(new Paragraph("\n").setMarginTop(7));

                // Columna derecha - Título y datos del cliente
                Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setWidth(UnitValue.createPercentValue(40));

                // Cuadro del RUC y título del documento
                Table rucTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Cell rucCell = new Cell().setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER).setPadding(8);
                rucCell.add(new Paragraph("R.U.C. Nº 20602292941").setFontSize(10).setBold());
                rucCell.add(new Paragraph(getDocumentTitle()).setFontSize(14).setBold().setMarginTop(5));

                String numeroDocumento = getNumeroDocumento(documentoDTO);
                if (numeroDocumento != null)
                        rucCell.add(new Paragraph(numeroDocumento).setFontSize(12).setBold().setMarginTop(5));

                rucTable.addCell(rucCell);
                rightCell.add(rucTable);
                rightCell.add(new Paragraph("\n").setMarginTop(1));

                // Cuadro de información del cliente
                Table clientInfoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Cell clientCell = new Cell().setBorder(new com.itextpdf.layout.borders.SolidBorder(1)).setPadding(8);

                String fechaEmision = getFechaEmision(documentoDTO);
                if (fechaEmision == null)
                        fechaEmision = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                String nombreCliente = getClienteNombre(documentoDTO);
                if (nombreCliente != null)
                        nombreCliente = nombreCliente.toUpperCase();
                else
                        nombreCliente = "CLIENTE";

                String numeroDoc = getClienteDocumento(documentoDTO) != null ? getClienteDocumento(documentoDTO)
                                : "00000000";
                String sucursal = getSucursalDescripcion(documentoDTO) != null ? getSucursalDescripcion(documentoDTO)
                                : "";

                Table innerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));

                innerTable.addCell(new Cell().add(new Paragraph("Fecha de emisión:").setBold().setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1));
                innerTable.addCell(new Cell().add(new Paragraph(fechaEmision).setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1));

                // Agregar fecha de vencimiento JUSTO DESPUÉS de fecha de emisión
                String fechaVencimiento = getFechaVencimiento(documentoDTO);
                if (fechaVencimiento != null) {
                        innerTable.addCell(
                                        new Cell().add(new Paragraph("Fecha de vencimiento:").setBold().setFontSize(9))
                                                        .setBorder(Border.NO_BORDER).setPadding(1));
                        innerTable.addCell(new Cell().add(new Paragraph(fechaVencimiento).setFontSize(9))
                                        .setBorder(Border.NO_BORDER).setPadding(1));
                }

                innerTable.addCell(new Cell().add(new Paragraph("Señor(es):").setBold().setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1));
                innerTable.addCell(new Cell().add(new Paragraph(nombreCliente).setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1));

                innerTable.addCell(new Cell()
                                .add(new Paragraph("Documento - " + getTipoDocumentoCliente(documentoDTO) + ":")
                                                .setBold().setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1));
                innerTable.addCell(new Cell().add(new Paragraph(numeroDoc).setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1));

                innerTable.addCell(new Cell().add(new Paragraph("Sucursal:").setBold().setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1));
                innerTable.addCell(new Cell().add(new Paragraph(sucursal).setFontSize(9))
                                .setBorder(Border.NO_BORDER).setPadding(1));

                clientCell.add(innerTable);
                clientInfoTable.addCell(clientCell);
                rightCell.add(clientInfoTable);

                headerTable.addCell(leftCell);
                headerTable.addCell(rightCell);

                document.add(headerTable);
                document.add(new Paragraph("").setMarginTop(1));
        }

        protected void addSummaryTable(Document document, T documentoDTO) {
                Table resumenTable = new Table(3).setWidth(UnitValue.createPercentValue(100));
                resumenTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

                Cell monedaCell = new Cell().setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER).setPadding(8);

                String moneda = getMoneda(documentoDTO) != null ? getMoneda(documentoDTO) + " - Dólar Americano"
                                : "USD - Dólar Americano";
                monedaCell.add(new Paragraph("Moneda: " + moneda).setFontSize(10));

                Cell fileCell = new Cell().setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER).setPadding(8);

                String fileVenta = getFileVenta(documentoDTO) != null ? getFileVenta(documentoDTO) : "N/A";
                fileCell.add(new Paragraph("File: " + fileVenta).setFontSize(10));

                Cell formaPagoCell = new Cell().setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER).setPadding(8);

                String formaPago = getFormaPagoDescripcion(documentoDTO) != null
                                ? getFormaPagoDescripcion(documentoDTO).toUpperCase()
                                : "NO ESPECIFICADO";
                formaPagoCell.add(new Paragraph("Forma de Pago: " + formaPago).setFontSize(10));

                resumenTable.addCell(monedaCell);
                resumenTable.addCell(fileCell);
                resumenTable.addCell(formaPagoCell);

                document.add(resumenTable);
                document.add(new Paragraph("").setMarginTop(2));
        }

        protected void addServicesTable(Document document, T documentoDTO, String userName) {
                float[] productColumnWidths = { 40f, 60f, 280f, 70f, 73f };
                Table servicesTable = new Table(UnitValue.createPointArray(productColumnWidths));
                servicesTable.setWidth(523f);
                servicesTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

                servicesTable.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold().setFontSize(10))
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER));

                servicesTable.addHeaderCell(new Cell().add(new Paragraph("Código").setBold().setFontSize(10))
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER));

                servicesTable.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBold().setFontSize(10))
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER));

                servicesTable.addHeaderCell(new Cell().add(new Paragraph("P.U.").setBold().setFontSize(10))
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER));

                servicesTable.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setFontSize(10))
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER));

                BigDecimal subTotalAmount = BigDecimal.ZERO;

                List<D> detalles = getDetalles(documentoDTO);
                List<D> detallesOrdenados = detalles.stream()
                                .sorted((d1, d2) -> {
                                        Long id1 = getDetalleId(d1);
                                        Long id2 = getDetalleId(d2);
                                        if (id1 == null)
                                                return 1;
                                        if (id2 == null)
                                                return -1;
                                        return id1.compareTo(id2);
                                })
                                .toList();

                for (D detalle : detallesOrdenados) {
                        String cantidad = String.valueOf(getCantidad(detalle) != null ? getCantidad(detalle) : 1);
                        servicesTable.addCell(new Cell().add(new Paragraph(cantidad).setFontSize(9))
                                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                        .setTextAlignment(TextAlignment.CENTER));

                        String codigoProducto = getProductoDescripcion(detalle) != null
                                        ? getProductoDescripcion(detalle)
                                        : "N/A";
                        servicesTable.addCell(new Cell().add(new Paragraph(codigoProducto).setFontSize(9))
                                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setKeepTogether(true));

                        Paragraph descripcionParagraph = new Paragraph(
                                        getDescripcionDetalle(detalle) != null ? getDescripcionDetalle(detalle) : "")
                                        .setFontSize(9)
                                        .setFixedLeading(11);

                        servicesTable.addCell(new Cell()
                                        .add(descripcionParagraph)
                                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                        .setMaxWidth(280f)
                                        .setPadding(3));

                        BigDecimal precioDetalle = getPrecio(detalle) != null ? getPrecio(detalle) : BigDecimal.ZERO;
                        String precioUnitario = "$ " + formatearNumeroConSeparadorMiles(precioDetalle);
                        servicesTable.addCell(new Cell().add(new Paragraph(precioUnitario).setFontSize(9))
                                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                        .setTextAlignment(TextAlignment.RIGHT)
                                        .setKeepTogether(true));

                        BigDecimal cantidadBD = getCantidad(detalle) != null ? new BigDecimal(getCantidad(detalle))
                                        : BigDecimal.ONE;
                        BigDecimal precioBD = getPrecio(detalle) != null ? getPrecio(detalle) : BigDecimal.ZERO;
                        BigDecimal totalDetalle = cantidadBD.multiply(precioBD);

                        String total = formatearNumeroConSeparadorMiles(totalDetalle);
                        servicesTable.addCell(new Cell().add(new Paragraph("$ " + total).setFontSize(9))
                                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                        .setTextAlignment(TextAlignment.RIGHT));

                        subTotalAmount = subTotalAmount.add(totalDetalle);
                }
                document.add(servicesTable);

                float[] totalsColumnWidths = { 40f, 60f, 260f, 90f, 73f };
                Table totalsTable = new Table(UnitValue.createPointArray(totalsColumnWidths));
                totalsTable.setWidth(523f);
                totalsTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

                BigDecimal costoEnvio = getCostoEnvio(documentoDTO) != null ? getCostoEnvio(documentoDTO)
                                : BigDecimal.ZERO;
                BigDecimal totalAmount = subTotalAmount.add(costoEnvio);
                String totalEnLetras = numberToTextConverter.convertirNumeroALetras(totalAmount.doubleValue(),
                                getMoneda(documentoDTO));

                totalsTable.addCell(new Cell(3, 1));

                Cell totalLetrasCell = new Cell(1, 2)
                                .add(new Paragraph("Son " + totalEnLetras).setFontSize(9))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER);
                totalsTable.addCell(totalLetrasCell);

                Cell subtotalLabelCell = new Cell()
                                .add(new Paragraph("SUBTOTAL").setFontSize(9))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER);
                totalsTable.addCell(subtotalLabelCell);

                String subtotal = formatearNumeroConSeparadorMiles(subTotalAmount);
                totalsTable.addCell(new Cell().add(new Paragraph("$ " + subtotal).setFontSize(9))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.RIGHT));

                String nombreUsuario = userName != null ? userName : "Usuario desconocido";
                int rowSpanCreador = showCostoEnvio() ? 2 : 1;
                totalsTable.addCell(new Cell(rowSpanCreador, 2)
                                .add(new Paragraph("CREADO POR: " + nombreUsuario).setFontSize(9))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.LEFT)
                                .setPadding(5));

                if (showCostoEnvio()) {
                        totalsTable.addCell(new Cell().add(new Paragraph("COSTO DE ENVIO").setFontSize(9))
                                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                        .setTextAlignment(TextAlignment.CENTER));

                        String costoEnvioStr = formatearNumeroConSeparadorMiles(costoEnvio);
                        totalsTable.addCell(new Cell().add(new Paragraph("$ " + costoEnvioStr).setFontSize(9))
                                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                        .setTextAlignment(TextAlignment.RIGHT));
                }

                totalsTable.addCell(new Cell().add(new Paragraph("PRECIO VENTA").setBold().setFontSize(9))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.CENTER));

                String totalStr = formatearNumeroConSeparadorMiles(totalAmount);
                totalsTable.addCell(new Cell().add(new Paragraph("$ " + totalStr).setFontSize(9))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setTextAlignment(TextAlignment.RIGHT));

                document.add(totalsTable);
                document.add(new Paragraph("\n"));
        }

        protected void addObservationsBox(Document document, T documentoDTO) {
                document.add(new Paragraph("\n"));

                float[] observationColumnWidths = { 523f };
                Table observationsTable = new Table(UnitValue.createPointArray(observationColumnWidths));
                observationsTable.setWidth(523f);
                observationsTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

                String observaciones = getObservaciones(documentoDTO) != null ? getObservaciones(documentoDTO) : "";

                Paragraph labelParagraph = new Paragraph()
                                .add(new com.itextpdf.layout.element.Text("OBSERVACIONES: ").setBold()).setFontSize(10);
                Paragraph observationsParagraph = new Paragraph(observaciones).setFontSize(10).setFixedLeading(11);

                observationsTable.addCell(new Cell()
                                .add(labelParagraph)
                                .add(observationsParagraph)
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1))
                                .setMaxWidth(523f)
                                .setPadding(3));

                document.add(observationsTable);
        }

        protected void addPageFooter(PdfDocument pdfDocument) {
                pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new com.itextpdf.kernel.events.IEventHandler() {
                        @Override
                        public void handleEvent(Event event) {
                                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                                PdfPage page = docEvent.getPage();
                                PdfDocument pdf = docEvent.getDocument();

                                try {
                                        PdfCanvas canvas = new PdfCanvas(page);
                                        com.itextpdf.kernel.font.PdfFont font8 = PdfFontFactory.createFont();
                                        com.itextpdf.kernel.font.PdfFont font9 = PdfFontFactory.createFont();

                                        float pageWidth = page.getPageSize().getWidth();
                                        float pageLeft = page.getPageSize().getLeft();

                                        int currentPageNumber = pdf.getPageNumber(page);
                                        int totalPages = pdf.getNumberOfPages();

                                        if (currentPageNumber == totalPages && showDisclaimer()) {
                                                float y1 = page.getPageSize().getBottom() + 35;
                                                String redText = "NO VÁLIDO PARA CRÉDITO FISCAL, SOLO PARA FINES DE COBRANZA";
                                                float redTextWidth = font9.getWidth(redText, 9);
                                                float redTextX = pageLeft + (pageWidth - redTextWidth) / 2;

                                                canvas.setColor(ColorConstants.RED, true);
                                                canvas.beginText()
                                                                .setFontAndSize(font9, 9)
                                                                .moveText(redTextX, y1)
                                                                .showText(redText)
                                                                .endText();
                                        }

                                        float y2 = page.getPageSize().getBottom() + 20;
                                        String footerText = getFooterText();
                                        float footerTextWidth = font8.getWidth(footerText, 8);
                                        float footerTextX = pageLeft + (pageWidth - footerTextWidth) / 2;

                                        canvas.setColor(ColorConstants.BLACK, true);
                                        canvas.beginText()
                                                        .setFontAndSize(font8, 8)
                                                        .moveText(footerTextX, y2)
                                                        .showText(footerText)
                                                        .endText();
                                } catch (Exception e) {
                                        System.err.println("Error al agregar el pie de página: " + e.getMessage());
                                }
                        }
                });
        }
}
