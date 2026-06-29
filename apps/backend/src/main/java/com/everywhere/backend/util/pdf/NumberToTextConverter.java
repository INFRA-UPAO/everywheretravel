package com.everywhere.backend.util.pdf;

import org.springframework.stereotype.Component;

@Component
public class NumberToTextConverter {
    
    
    public String convertirNumeroALetras(double numero, String moneda) {
        if (numero == 0)
            return "CERO " + obtenerNombreMoneda(moneda);

        // Separar parte entera y decimales
        long parteEntera = (long) numero;
        int decimales = (int) Math.round((numero - parteEntera) * 100);

        String parteEnteraEnLetras = convertirEnteroALetras(parteEntera);
        String nombreMoneda = obtenerNombreMoneda(moneda);

        // Mantener todo en mayúsculas
        String resultado;
        if (decimales == 0)
            resultado = parteEnteraEnLetras + " CON 00/100 " + nombreMoneda;
        else
            resultado = parteEnteraEnLetras + " CON " + String.format("%02d", decimales) + "/100 " + nombreMoneda;

        return resultado;
    }

    private String obtenerNombreMoneda(String moneda) {
        if (moneda == null)
            return "Dólares Americanos";

        switch (moneda.toUpperCase()) {
            case "USD":
                return "Dólares Americanos";
            case "PEN":
                return "Soles";
            case "EUR":
                return "Euros";
            default:
                return "Dólares Americanos";
        }
    }

    private String convertirEnteroALetras(long numero) {
        if (numero == 0)
            return "CERO";
        if (numero == 1)
            return "UNO";

        String resultado = "";
        // Millones
        if (numero >= 1000000) {
            long millones = numero / 1000000;
            if (millones == 1)
                resultado += "UN MILLÓN ";
            else
                resultado += convertirGrupoTresCifras((int) millones) + " MILLONES ";
            numero %= 1000000;
        }

        // Miles
        if (numero >= 1000) {
            long miles = numero / 1000;
            if (miles == 1)
                resultado += "MIL ";
            else
                resultado += convertirGrupoTresCifras((int) miles) + " MIL ";
            numero %= 1000;
        }
        // Centenas, decenas y unidades
        if (numero > 0)
            resultado += convertirGrupoTresCifras((int) numero);
        return resultado.trim();
    }

    private String convertirGrupoTresCifras(int numero) {
        if (numero == 0)
            return "";

        String[] unidades = { "", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE" };
        String[] especiales = { "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISÉIS", "DIECISIETE",
                "DIECIOCHO", "DIECINUEVE" };
        String[] decenas = { "", "", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA",
                "NOVENTA" };
        String[] centenas = { "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", "SEISCIENTOS",
                "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS" };

        String resultado = "";

        // Centenas
        int c = numero / 100;
        if (c > 0) {
            if (numero == 100)
                resultado += "CIEN";
            else
                resultado += centenas[c] + " ";
        }

        // Decenas y unidades
        int resto = numero % 100;
        if (resto >= 10 && resto <= 19)
            resultado += especiales[resto - 10];
        else {
            int d = resto / 10;
            int u = resto % 10;

            if (d == 2 && u > 0)
                resultado += "VEINTI" + unidades[u];
            else {
                if (d > 0) {
                    resultado += decenas[d];
                    if (u > 0)
                        resultado += " Y " + unidades[u];
                } else if (u > 0)
                    resultado += unidades[u];
            }
        }
        return resultado.trim();
    }


}
