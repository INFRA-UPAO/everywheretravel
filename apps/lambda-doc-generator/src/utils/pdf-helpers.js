export const COLORS = {
  black: '#000000',
  white: '#FFFFFF',
  lightGray: '#D3D3D3',
  red: '#FF0000',
  headerBorder: '#000000'
};

export const COMPANY = {
  name: 'EVERYWHERE TRAVEL SAC',
  ruc: '20602292941',
  address: "MZ.J' LTE.10 URB.SOLILUZ, TRUJILLO, PERU",
  phone: '044 729-728',
  mobile: '+51 944 493 851 / 947 755 582'
};

export const PAGE = {
  width: 595.28,
  height: 841.89,
  marginLeft: 36,
  marginRight: 36,
  marginTop: 36,
  marginBottom: 50,
  get contentWidth() {
    return this.width - this.marginLeft - this.marginRight;
  }
};

export function formatNumber(value) {
  if (value == null || isNaN(value)) return '0.00';
  const num = Number(value);
  const parts = num.toFixed(2).split('.');
  const intPart = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  return `${intPart}.${parts[1]}`;
}

export function getCurrencySymbol(moneda) {
  if (!moneda) return '$';
  switch (moneda.toUpperCase()) {
    case 'PEN': return 'S/';
    case 'EUR': return 'EUR';
    case 'USD':
    default: return '$';
  }
}

export function getCurrencyDescription(moneda) {
  if (!moneda) return 'USD - Dolar Americano';
  switch (moneda.toUpperCase()) {
    case 'PEN': return 'PEN - Sol Peruano';
    case 'EUR': return 'EUR - Euro';
    case 'USD':
    default: return 'USD - Dolar Americano';
  }
}

export function drawHorizontalLine(doc, y, x1 = PAGE.marginLeft, x2 = PAGE.width - PAGE.marginRight) {
  doc.strokeColor(COLORS.headerBorder)
    .lineWidth(0.5)
    .moveTo(x1, y)
    .lineTo(x2, y)
    .stroke();
}

export function drawRect(doc, x, y, w, h, options = {}) {
  const { fill, stroke = COLORS.headerBorder, lineWidth = 0.5 } = options;
  doc.lineWidth(lineWidth).strokeColor(stroke);
  if (fill) {
    doc.fillAndStroke(fill, stroke);
  }
  doc.rect(x, y, w, h).stroke();
}

export function drawFilledRect(doc, x, y, w, h, fillColor) {
  doc.save();
  doc.rect(x, y, w, h)
    .fillColor(fillColor)
    .fill();
  doc.restore();
  doc.rect(x, y, w, h)
    .strokeColor(COLORS.headerBorder)
    .lineWidth(0.5)
    .stroke();
  doc.fillColor(COLORS.black);
}
