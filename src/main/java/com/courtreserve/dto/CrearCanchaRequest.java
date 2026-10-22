package com.courtreserve.dto;

import java.math.BigDecimal;

import com.courtreserve.model.TipoCancha;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearCanchaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotNull(message = "El tipo de cancha es obligatorio")
        TipoCancha tipo,

        @NotNull(message = "El precio por hora es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
        BigDecimal precioHora) {
}
