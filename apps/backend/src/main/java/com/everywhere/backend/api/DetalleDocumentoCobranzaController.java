package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaRequestDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DetalleDocumentoCobranzaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-documento-cobranza")
@RequiredArgsConstructor
public class DetalleDocumentoCobranzaController {

    private final DetalleDocumentoCobranzaService detalleService;

    @GetMapping
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<List<DetalleDocumentoCobranzaResponseDTO>> getAllDetalles() { 
        return ResponseEntity.ok(detalleService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<DetalleDocumentoCobranzaResponseDTO> getDetalleById(@PathVariable Long id) {
        return ResponseEntity.ok(detalleService.findById(id));
    }

    @GetMapping("/documento-cobranza/{documentoId}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<List<DetalleDocumentoCobranzaResponseDTO>> getDetallesByDocumentoCobranza(@PathVariable Long documentoId) {
        return ResponseEntity.ok(detalleService.findByDocumentoCobranzaId(documentoId));
    }

    @PostMapping
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "CREATE")
    public ResponseEntity<DetalleDocumentoCobranzaResponseDTO> createDetalle(@Valid @RequestBody DetalleDocumentoCobranzaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleService.save(dto));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "UPDATE")
    public ResponseEntity<DetalleDocumentoCobranzaResponseDTO> updateDetalle(
            @PathVariable Long id, @Valid @RequestBody DetalleDocumentoCobranzaRequestDTO detalleDocumentoCobranzaRequestDTO) { 
        return ResponseEntity.ok(detalleService.patch(id, detalleDocumentoCobranzaRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "DELETE")
    public ResponseEntity<Void> deleteDetalle(@PathVariable Long id) {
        detalleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}