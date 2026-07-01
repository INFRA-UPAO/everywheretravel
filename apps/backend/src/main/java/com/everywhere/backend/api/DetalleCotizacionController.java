package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DetalleCotizacionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/detalles-cotizacion")
public class DetalleCotizacionController {

    private final DetalleCotizacionService detalleCotizacionService;

    @GetMapping
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<DetalleCotizacionResponseDto>> getAll() {
        return ResponseEntity.ok(detalleCotizacionService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<DetalleCotizacionResponseDto> getById(@PathVariable int id) {
        return ResponseEntity.ok(detalleCotizacionService.findById(id));
    }

    @GetMapping("/cotizacion/{cotizacionId}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<DetalleCotizacionResponseDto>> getByCotizacionId(@PathVariable int cotizacionId) {
        return ResponseEntity.ok(detalleCotizacionService.findByCotizacionId(cotizacionId));
    }

    @PostMapping("/cotizacion/{cotizacionId}")
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<DetalleCotizacionResponseDto> create(
            @PathVariable int cotizacionId, @RequestBody DetalleCotizacionRequestDto detalleCotizacionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleCotizacionService.create(detalleCotizacionRequestDto, cotizacionId));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<DetalleCotizacionResponseDto> patch(
            @PathVariable int id, @RequestBody DetalleCotizacionRequestDto detalleCotizacionRequestDto) {
        return ResponseEntity.ok(detalleCotizacionService.patch(id, detalleCotizacionRequestDto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        detalleCotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}