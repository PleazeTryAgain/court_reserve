package com.courtreserve.dto;

import java.math.BigDecimal;

import com.courtreserve.model.Cancha;
import com.courtreserve.model.TipoCancha;

public record CanchaResponse(
        Long id,
        String nombre,
        TipoCancha tipo,
        BigDecimal precioHora,
        boolean activa) {

    public static CanchaResponse fromEntity(Cancha cancha) {

        return new CanchaResponse(
                cancha.getId(),
                cancha.getNombre(),
                cancha.getTipo(),
                cancha.getPrecioHora(),
                cancha.isActiva());
    }
}
