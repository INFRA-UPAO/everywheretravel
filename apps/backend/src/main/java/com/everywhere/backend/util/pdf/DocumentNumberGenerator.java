package com.everywhere.backend.util.pdf;

import org.springframework.stereotype.Component;

/**
 * Generador de números de documentos con formato {PREFIX}{SERIE}-{CORRELATIVO}
 * Ejemplos:
 * - Documento Cobranza: DC01-000000001, DC01-000000002, ...
 * - Recibo: R01-000000001, R01-000000002, ...
 */
@Component
public class DocumentNumberGenerator {

    /**
     * Genera el siguiente número de documento basado en el último número encontrado
     * 
     * @param lastNumber Último número de documento (ej: "DC01-000000150" o null si no existe)
     * @param prefix Prefijo del documento (ej: "DC" para Documento Cobranza, "R" para Recibo)
     * @param defaultSerieNumber Número de serie por defecto (ej: 1 para empezar en DC01 o R01)
     * @param defaultCorrelativo Correlativo inicial por defecto (ej: 1)
     * @return Siguiente número de documento
     */
    public String generateNextNumber(String lastNumber, String prefix, int defaultSerieNumber, int defaultCorrelativo) {
        if (lastNumber == null || lastNumber.trim().isEmpty()) {
            // No hay número previo, retornar el número inicial
            String serie = String.format("%s%02d", prefix, defaultSerieNumber);
            return String.format("%s-%09d", serie, defaultCorrelativo);
        }

        String[] parts = lastNumber.split("-");
        if (parts.length != 2) {
            // Formato inválido, retornar número inicial
            String serie = String.format("%s%02d", prefix, defaultSerieNumber);
            return String.format("%s-%09d", serie, defaultCorrelativo);
        }

        String serieStr = parts[0]; // Ejemplo: "DC01" o "R01"
        int correlativo = Integer.parseInt(parts[1]);
        int nextCorrelativo = correlativo + 1;

        // Si el correlativo supera 999999999, incrementar la serie
        if (nextCorrelativo > 999999999) {
            // Extraer el prefijo (DC o R) y el número de serie (01)
            String currentPrefix = serieStr.replaceAll("\\d+$", ""); // "DC" o "R"
            String serieNumStr = serieStr.replaceAll("^\\D+", ""); // "01"
            int serieNum = Integer.parseInt(serieNumStr);
            int nextSerieNum = serieNum + 1;
            
            // Formato: Prefijo + número de serie con 2 dígitos
            String nextSerie = String.format("%s%02d", currentPrefix, nextSerieNum);
            return String.format("%s-%09d", nextSerie, 1);
        }

        return String.format("%s-%09d", serieStr, nextCorrelativo);
    }

    /**
     * Genera el siguiente número de Documento de Cobranza
     * 
     * @param lastNumber Último número encontrado o null
     * @return Número en formato DC01-000000001
     */
    public String generateNextDocumentoCobranzaNumber(String lastNumber) {
        return generateNextNumber(lastNumber, "DC", 1, 1);
    }

    /**
     * Genera el siguiente número de Recibo
     * 
     * @param lastNumber Último número encontrado o null
     * @return Número en formato R01-000000001
     */
    public String generateNextReciboNumber(String lastNumber) {
        return generateNextNumber(lastNumber, "R", 1, 1);
    }
}
