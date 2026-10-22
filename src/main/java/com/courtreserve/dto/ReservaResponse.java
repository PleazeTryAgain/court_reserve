package com.courtreserve.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.courtreserve.model.EstadoReserva;
import com.courtreserve.model.Reserva;

public record ReservaResponse(
        Long id,
        Long usuarioId,
        String usuarioNombre,
        Long canchaId,
        String canchaNombre,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        BigDecimal precioTotal,
        EstadoReserva estado) {

    /**
     * Requiere que la reserva tenga cargados el usuario y la cancha,
     * como la devuelven las consultas {@code ...ConRelaciones} del repositorio.
     */
    public static ReservaResponse fromEntity(Reserva reserva) {

        return new ReservaResponse(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getUsuario().getNombre(),
                reserva.getCancha().getId(),
                reserva.getCancha().getNombre(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getPrecioTotal(),
                reserva.getEstado());
    }
}
