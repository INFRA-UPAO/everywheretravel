const UNIDADES = [
  '', 'UNO', 'DOS', 'TRES', 'CUATRO', 'CINCO',
  'SEIS', 'SIETE', 'OCHO', 'NUEVE'
];

const ESPECIALES = [
  'DIEZ', 'ONCE', 'DOCE', 'TRECE', 'CATORCE', 'QUINCE',
  'DIECISEIS', 'DIECISIETE', 'DIECIOCHO', 'DIECINUEVE'
];

const DECENAS = [
  '', '', 'VEINTE', 'TREINTA', 'CUARENTA', 'CINCUENTA',
  'SESENTA', 'SETENTA', 'OCHENTA', 'NOVENTA'
];

const CENTENAS = [
  '', 'CIENTO', 'DOSCIENTOS', 'TRESCIENTOS', 'CUATROCIENTOS',
  'QUINIENTOS', 'SEISCIENTOS', 'SETECIENTOS', 'OCHOCIENTOS', 'NOVECIENTOS'
];

function convertirGrupoTresCifras(numero) {
  if (numero === 0) return '';

  let resultado = '';

  const c = Math.floor(numero / 100);
  if (c > 0) {
    if (numero === 100) {
      resultado += 'CIEN';
    } else {
      resultado += CENTENAS[c] + ' ';
    }
  }

  const resto = numero % 100;
  if (resto >= 10 && resto <= 19) {
    resultado += ESPECIALES[resto - 10];
  } else {
    const d = Math.floor(resto / 10);
    const u = resto % 10;

    if (d === 2 && u > 0) {
      resultado += 'VEINTI' + UNIDADES[u];
    } else {
      if (d > 0) {
        resultado += DECENAS[d];
        if (u > 0) {
          resultado += ' Y ' + UNIDADES[u];
        }
      } else if (u > 0) {
        resultado += UNIDADES[u];
      }
    }
  }

  return resultado.trim();
}

function convertirEnteroALetras(numero) {
  if (numero === 0) return 'CERO';
  if (numero === 1) return 'UNO';

  let resultado = '';
  let n = numero;

  if (n >= 1000000) {
    const millones = Math.floor(n / 1000000);
    if (millones === 1) {
      resultado += 'UN MILLON ';
    } else {
      resultado += convertirGrupoTresCifras(millones) + ' MILLONES ';
    }
    n %= 1000000;
  }

  if (n >= 1000) {
    const miles = Math.floor(n / 1000);
    if (miles === 1) {
      resultado += 'MIL ';
    } else {
      resultado += convertirGrupoTresCifras(miles) + ' MIL ';
    }
    n %= 1000;
  }

  if (n > 0) {
    resultado += convertirGrupoTresCifras(n);
  }

  return resultado.trim();
}

function obtenerNombreMoneda(moneda) {
  if (!moneda) return 'Dolares Americanos';

  switch (moneda.toUpperCase()) {
    case 'USD': return 'Dolares Americanos';
    case 'PEN': return 'Soles';
    case 'EUR': return 'Euros';
    default: return 'Dolares Americanos';
  }
}

export function convertirNumeroALetras(numero, moneda) {
  if (numero === 0) {
    return 'CERO ' + obtenerNombreMoneda(moneda);
  }

  const parteEntera = Math.floor(numero);
  const decimales = Math.round((numero - parteEntera) * 100);

  const parteEnteraEnLetras = convertirEnteroALetras(parteEntera);
  const nombreMoneda = obtenerNombreMoneda(moneda);

  const decimalesStr = String(decimales).padStart(2, '0');

  return `${parteEnteraEnLetras} CON ${decimalesStr}/100 ${nombreMoneda}`;
}
