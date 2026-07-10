package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.HistorialCotizacionRequestDTO;
import com.everywhere.backend.model.dto.HistorialCotizacionResponseDTO;
import com.everywhere.backend.model.dto.HistorialCotizacionSimpleDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.HistorialCotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial-cotizaciones")
@RequiredArgsConstructor
public class HistorialCotizacionController {

    private final HistorialCotizacionService historialCotizacionService;

    @GetMapping
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<HistorialCotizacionResponseDTO>> findAll() {
        return ResponseEntity.ok(historialCotizacionService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<HistorialCotizacionResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(historialCotizacionService.findById(id));
    }

    @GetMapping("/cotizacion/{cotizacionId}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<HistorialCotizacionSimpleDTO>> findByCotizacionId(@PathVariable Integer cotizacionId) {
        return ResponseEntity.ok(historialCotizacionService.findByCotizacionId(cotizacionId));
    }

    @PostMapping
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<HistorialCotizacionResponseDTO> create(
            @RequestBody HistorialCotizacionRequestDTO historialCotizacionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historialCotizacionService.save(historialCotizacionRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<HistorialCotizacionResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody HistorialCotizacionRequestDTO historialCotizacionRequestDTO) {
        return ResponseEntity.ok(historialCotizacionService.update(id, historialCotizacionRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        historialCotizacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}