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
import org.springframework.web.bind.annotation.RestController;

import com.courtreserve.dto.CrearReservaRequest;
import com.courtreserve.dto.ReservaResponse;
import com.courtreserve.model.Reserva;
import com.courtreserve.service.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(
            @Valid @RequestBody CrearReservaRequest request) {

        Reserva reservaCreada = reservaService.crearReserva(
                request.getUsuarioId(),
                request.getCanchaId(),
                request.getFecha(),
                request.getHoraInicio(),
                request.getHoraFin());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ReservaResponse.fromEntity(reservaCreada));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {

        List<ReservaResponse> reservas = reservaService
                .listarReservas()
                .stream()
                .map(ReservaResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(reservas);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(
            @PathVariable Long id) {

        Reserva reserva = reservaService.cancelarReserva(id);

        return ResponseEntity.ok(
                ReservaResponse.fromEntity(reserva));
    }
}