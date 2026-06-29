import PDFDocument from 'pdfkit';
import {
  COLORS, COMPANY, PAGE,
  formatNumber, getCurrencySymbol, getCurrencyDescription,
  drawRect, drawFilledRect, drawHorizontalLine
} from '../utils/pdf-helpers.js';
import { convertirNumeroALetras } from '../utils/number-to-text.js';

export async function generateRecibo(data) {
  return new Promise((resolve, reject) => {
    try {
      const doc = new PDFDocument({
        size: 'A4',
        margins: {
          top: PAGE.marginTop,
          bottom: PAGE.marginBottom,
          left: PAGE.marginLeft,
          right: PAGE.marginRight
        },
        bufferPages: true,
        info: {
          Title: `Recibo ${data.numero || ''}`,
          Author: COMPANY.name,
          Subject: 'Recibo'
        }
      });

      const chunks = [];
      doc.on('data', (chunk) => chunks.push(chunk));
      doc.on('end', () => resolve(Buffer.concat(chunks)));
      doc.on('error', reject);

      renderRecibo(doc, data);

      doc.end();
    } catch (err) {
      reject(err);
    }
  });
}

function renderRecibo(doc, data) {
  const startY = addCompanyHeader(doc, data);
  const afterSummary = addSummaryTable(doc, startY, data);
  const afterServices = addServicesTable(doc, afterSummary, data);
  addObservationsBox(doc, afterServices, data);
  addPageFooter(doc);
}

function addCompanyHeader(doc, data) {
  const leftColWidth = PAGE.contentWidth * 0.58;
  const rightColWidth = PAGE.contentWidth * 0.40;
  const gapCol = PAGE.contentWidth * 0.02;
  const rightColX = PAGE.marginLeft + leftColWidth + gapCol;

  let y = PAGE.marginTop;

  doc.fontSize(16).font('Helvetica-Bold')
    .text('EVERYWHERE TRAVEL', PAGE.marginLeft, y, { width: leftColWidth });
  y += 24;

  doc.fontSize(11).font('Helvetica-Bold')
    .text(COMPANY.name, PAGE.marginLeft, y, { width: leftColWidth });
  y += 16;

  doc.fontSize(9).font('Helvetica')
    .text(COMPANY.address, PAGE.marginLeft, y, { width: leftColWidth });
  y += 13;
  doc.text(`Telefono: ${COMPANY.phone}`, PAGE.marginLeft, y, { width: leftColWidth });
  y += 13;
  doc.text(`Celular: ${COMPANY.mobile}`, PAGE.marginLeft, y, { width: leftColWidth });
  y += 20;

  let ryStart = PAGE.marginTop;
  const rucBoxH = 70;
  drawRect(doc, rightColX, ryStart, rightColWidth, rucBoxH);

  doc.fontSize(10).font('Helvetica-Bold')
    .text(`R.U.C. N. ${COMPANY.ruc}`, rightColX, ryStart + 10, {
      width: rightColWidth, align: 'center'
    });
  doc.fontSize(14).font('Helvetica-Bold')
    .text('RECIBO', rightColX, ryStart + 28, {
      width: rightColWidth, align: 'center'
    });

  if (data.numero) {
    doc.fontSize(12).font('Helvetica-Bold')
      .text(data.numero, rightColX, ryStart + 48, {
        width: rightColWidth, align: 'center'
      });
  }

  const clientBoxY = ryStart + rucBoxH + 6;
  const fechaEmision = data.fecha || new Date().toLocaleDateString('es-PE');
  const clienteNombre = (data.cliente || 'CLIENTE').toUpperCase();
  const clienteDoc = data.clienteDocumento || '00000000';
  const tipoDocCliente = data.tipoDocumentoCliente || 'DNI';
  const sucursal = data.sucursal || '';
  const fechaVencimiento = data.fechaVencimiento || null;

  const clientLines = [
    { label: 'Fecha de emision:', value: fechaEmision }
  ];
  if (fechaVencimiento) {
    clientLines.push({ label: 'Fecha de vencimiento:', value: fechaVencimiento });
  }
  clientLines.push(
    { label: 'Senor(es):', value: clienteNombre },
    { label: `Documento - ${tipoDocCliente}:`, value: clienteDoc },
    { label: 'Sucursal:', value: sucursal }
  );

  const lineH = 15;
  const clientBoxH = clientLines.length * lineH + 16;
  drawRect(doc, rightColX, clientBoxY, rightColWidth, clientBoxH);

  let cy = clientBoxY + 8;
  const labelW = rightColWidth * 0.48;
  const valW = rightColWidth * 0.48;
  for (const line of clientLines) {
    doc.fontSize(9).font('Helvetica-Bold')
      .text(line.label, rightColX + 6, cy, { width: labelW, continued: false });
    doc.fontSize(9).font('Helvetica')
      .text(line.value, rightColX + 6 + labelW, cy, { width: valW });
    cy += lineH;
  }

  return Math.max(y, clientBoxY + clientBoxH) + 10;
}

function addSummaryTable(doc, startY, data) {
  const y = startY;
  const colW = PAGE.contentWidth / 3;
  const h = 28;

  const moneda = getCurrencyDescription(data.moneda);
  const fileVenta = data.fileVenta || 'N/A';
  const formaPago = (data.formaPago || 'NO ESPECIFICADO').toUpperCase();

  for (let i = 0; i < 3; i++) {
    const x = PAGE.marginLeft + i * colW;
    drawRect(doc, x, y, colW, h);
  }

  doc.fontSize(10).font('Helvetica');
  doc.text(`Moneda: ${moneda}`, PAGE.marginLeft + 4, y + 8, { width: colW - 8, align: 'center' });
  doc.text(`File: ${fileVenta}`, PAGE.marginLeft + colW + 4, y + 8, { width: colW - 8, align: 'center' });
  doc.text(`Forma de Pago: ${formaPago}`, PAGE.marginLeft + 2 * colW + 4, y + 8, { width: colW - 8, align: 'center' });

  return y + h + 6;
}

function addServicesTable(doc, startY, data) {
  const detalles = (data.detalles || []).slice().sort((a, b) => (a.id || 0) - (b.id || 0));
  if (detalles.length === 0) return startY;

  const symbol = getCurrencySymbol(data.moneda);
  const colWidths = [40, 60, 280, 70, 73];
  const tableW = colWidths.reduce((s, w) => s + w, 0);
  const xs = [];
  let acc = PAGE.marginLeft;
  for (const w of colWidths) {
    xs.push(acc);
    acc += w;
  }

  let y = startY;

  const headerH = 20;
  const headers = ['Cant.', 'Codigo', 'Descripcion', 'P.U.', 'Total'];
  for (let i = 0; i < headers.length; i++) {
    drawFilledRect(doc, xs[i], y, colWidths[i], headerH, COLORS.lightGray);
    doc.fontSize(10).font('Helvetica-Bold').fillColor(COLORS.black)
      .text(headers[i], xs[i] + 3, y + 5, { width: colWidths[i] - 6, align: 'center' });
  }
  y += headerH;

  let subtotal = 0;
  for (const det of detalles) {
    const cantidad = det.cantidad != null ? det.cantidad : 1;
    const precio = det.precioUnitario != null ? det.precioUnitario : (det.precio != null ? det.precio : 0);
    const totalDet = cantidad * precio;
    subtotal += totalDet;

    const codigo = det.productoDescripcion || det.codigo || 'N/A';
    const descripcion = det.descripcion || '';

    const descHeight = doc.fontSize(9).font('Helvetica')
      .heightOfString(descripcion, { width: colWidths[2] - 6 });
    const rowH = Math.max(18, descHeight + 8);

    if (y + rowH > PAGE.height - PAGE.marginBottom - 100) {
      doc.addPage();
      y = PAGE.marginTop;
    }

    for (let i = 0; i < colWidths.length; i++) {
      drawRect(doc, xs[i], y, colWidths[i], rowH);
    }

    doc.fontSize(9).font('Helvetica').fillColor(COLORS.black);
    doc.text(String(cantidad), xs[0] + 3, y + 4, { width: colWidths[0] - 6, align: 'center' });
    doc.text(codigo, xs[1] + 3, y + 4, { width: colWidths[1] - 6, align: 'center' });
    doc.text(descripcion, xs[2] + 3, y + 4, { width: colWidths[2] - 6 });
    doc.text(`${symbol} ${formatNumber(precio)}`, xs[3] + 3, y + 4, { width: colWidths[3] - 6, align: 'right' });
    doc.text(`${symbol} ${formatNumber(totalDet)}`, xs[4] + 3, y + 4, { width: colWidths[4] - 6, align: 'right' });

    y += rowH;
  }

  y = addTotalsTable(doc, y, data, subtotal, 0, symbol, false);

  return y;
}

function addTotalsTable(doc, startY, data, subtotal, costoEnvio, symbol, showCostoEnvioRow) {
  const total = subtotal + costoEnvio;
  const totalEnLetras = convertirNumeroALetras(total, data.moneda);

  const colWidths = [40, 60, 260, 90, 73];
  const tableW = colWidths.reduce((s, w) => s + w, 0);
  const xs = [];
  let acc = PAGE.marginLeft;
  for (const w of colWidths) {
    xs.push(acc);
    acc += w;
  }

  let y = startY;
  const rowH = 18;
  const totalRows = showCostoEnvioRow ? 3 : 2;

  if (y + rowH * totalRows > PAGE.height - PAGE.marginBottom - 60) {
    doc.addPage();
    y = PAGE.marginTop;
  }

  drawRect(doc, xs[0], y, colWidths[0], rowH * totalRows);

  const sonWidth = colWidths[1] + colWidths[2];
  drawRect(doc, xs[1], y, sonWidth, rowH);
  doc.fontSize(9).font('Helvetica').fillColor(COLORS.black)
    .text(`Son ${totalEnLetras}`, xs[1] + 4, y + 4, { width: sonWidth - 8, align: 'center' });

  drawRect(doc, xs[3], y, colWidths[3], rowH);
  doc.fontSize(9).font('Helvetica')
    .text('SUBTOTAL', xs[3] + 3, y + 4, { width: colWidths[3] - 6, align: 'center' });

  drawRect(doc, xs[4], y, colWidths[4], rowH);
  doc.fontSize(9).font('Helvetica')
    .text(`${symbol} ${formatNumber(subtotal)}`, xs[4] + 3, y + 4, { width: colWidths[4] - 6, align: 'right' });

  y += rowH;

  const creadorRows = showCostoEnvioRow ? 2 : 1;
  const creadorH = rowH * creadorRows;
  drawRect(doc, xs[1], y, sonWidth, creadorH);
  const creador = data.creador || 'Usuario desconocido';
  doc.fontSize(9).font('Helvetica')
    .text(`CREADO POR: ${creador}`, xs[1] + 5, y + 4, { width: sonWidth - 10, align: 'left' });

  if (showCostoEnvioRow) {
    drawRect(doc, xs[3], y, colWidths[3], rowH);
    doc.text('COSTO DE ENVIO', xs[3] + 3, y + 4, { width: colWidths[3] - 6, align: 'center' });
    drawRect(doc, xs[4], y, colWidths[4], rowH);
    doc.text(`${symbol} ${formatNumber(costoEnvio)}`, xs[4] + 3, y + 4, { width: colWidths[4] - 6, align: 'right' });
    y += rowH;
  }

  drawRect(doc, xs[3], y, colWidths[3], rowH);
  doc.fontSize(9).font('Helvetica-Bold')
    .text('PRECIO VENTA', xs[3] + 3, y + 4, { width: colWidths[3] - 6, align: 'center' });
  drawRect(doc, xs[4], y, colWidths[4], rowH);
  doc.fontSize(9).font('Helvetica')
    .text(`${symbol} ${formatNumber(total)}`, xs[4] + 3, y + 4, { width: colWidths[4] - 6, align: 'right' });

  y += rowH + 10;
  return y;
}

function addObservationsBox(doc, startY, data) {
  let y = startY + 10;
  const observaciones = data.observaciones || '';
  const boxW = 523;
  const boxX = PAGE.marginLeft;

  const textH = doc.fontSize(10).font('Helvetica')
    .heightOfString(observaciones || ' ', { width: boxW - 10 });
  const boxH = Math.max(40, textH + 30);

  if (y + boxH > PAGE.height - PAGE.marginBottom - 30) {
    doc.addPage();
    y = PAGE.marginTop;
  }

  drawRect(doc, boxX, y, boxW, boxH);

  doc.fontSize(10).font('Helvetica-Bold').fillColor(COLORS.black)
    .text('OBSERVACIONES: ', boxX + 5, y + 5, { width: boxW - 10, continued: false });
  doc.fontSize(10).font('Helvetica')
    .text(observaciones, boxX + 5, y + 20, { width: boxW - 10, lineGap: 1 });
}

function addPageFooter(doc) {
  const range = doc.bufferedPageRange();
  for (let i = range.start; i < range.start + range.count; i++) {
    doc.switchToPage(i);

    const footerText = 'Representacion Impresa de RECIBO';
    doc.fontSize(8).font('Helvetica').fillColor(COLORS.black)
      .text(footerText, PAGE.marginLeft, PAGE.height - 30, {
        width: PAGE.contentWidth,
        align: 'center'
      });
  }
}
