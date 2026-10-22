package com.courtreserve.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record CrearReservaRequest(

        @NotNull(message = "El usuario es obligatorio")
        Long usuarioId,

        @NotNull(message = "La cancha es obligatoria")
        Long canchaId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "La hora inicial es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora final es obligatoria")
        LocalTime horaFin) {
}
