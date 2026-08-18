package com.courtreserve.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courtreserve.dto.CanchaResponse;
import com.courtreserve.dto.CrearCanchaRequest;
import com.courtreserve.model.Cancha;
import com.courtreserve.service.CanchaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/canchas")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @PostMapping
    public ResponseEntity<CanchaResponse> crear(
            @Valid @RequestBody CrearCanchaRequest request) {

        Cancha cancha = new Cancha(
                request.getNombre(),
                request.getTipo(),
                request.getPrecioHora(),
                true);

        Cancha canchaCreada = canchaService.crearCancha(cancha);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CanchaResponse.fromEntity(canchaCreada));
    }

    @GetMapping
    public ResponseEntity<List<CanchaResponse>> listar() {

        List<CanchaResponse> canchas = canchaService
                .listarCanchas()
                .stream()
                .map(CanchaResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(canchas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CanchaResponse> buscarPorId(
            @PathVariable Long id) {

        Cancha cancha = canchaService.buscarPorId(id);

        return ResponseEntity.ok(
                CanchaResponse.fromEntity(cancha));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CanchaResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activa) {

        Cancha cancha = canchaService.cambiarEstado(id, activa);

        return ResponseEntity.ok(
                CanchaResponse.fromEntity(cancha));
    }
}