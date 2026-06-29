package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DocumentoCobranzaService;
import com.everywhere.backend.service.ReciboService;
import com.everywhere.backend.service.aws.SqsDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf")
public class PdfController {

    private final DocumentoCobranzaService documentoCobranzaService;
    private final ReciboService reciboService;
    private final SqsDocumentService sqsDocumentService;

    @GetMapping("/documento-cobranza/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<Map<String, Object>> generateDocumentoCobranzaPdf(@PathVariable Long id) {

        try {
            DocumentoCobranzaResponseDTO documentoDto = documentoCobranzaService.findById(id);

            if (documentoDto == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("serie", documentoDto.getSerie());
            data.put("correlativo", documentoDto.getCorrelativo());
            sqsDocumentService.sendPdfGenerationMessage("DOCUMENTO_COBRANZA", id.intValue(), data);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Generacion de PDF en proceso");
            response.put("documentoId", id);
            response.put("tipo", "DOCUMENTO_COBRANZA");

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Error al solicitar generacion de PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/recibo/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<Map<String, Object>> generateReciboPdf(@PathVariable Integer id) {

        try {
            ReciboResponseDTO reciboDto = reciboService.findById(id);

            if (reciboDto == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("serie", reciboDto.getSerie());
            data.put("correlativo", reciboDto.getCorrelativo());
            sqsDocumentService.sendPdfGenerationMessage("RECIBO", id, data);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Generacion de PDF en proceso");
            response.put("reciboId", id);
            response.put("tipo", "RECIBO");

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Error al solicitar generacion de PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
