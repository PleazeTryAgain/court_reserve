package com.courtreserve.dto;

import java.math.BigDecimal;

import com.courtreserve.model.Cancha;
import com.courtreserve.model.TipoCancha;

public class CanchaResponse {

    private Long id;
    private String nombre;
    private TipoCancha tipo;
    private BigDecimal precioHora;
    private boolean activa;

    public CanchaResponse() {
    }

    public CanchaResponse(
            Long id,
            String nombre,
            TipoCancha tipo,
            BigDecimal precioHora,
            boolean activa) {

        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precioHora = precioHora;
        this.activa = activa;
    }

    public static CanchaResponse fromEntity(Cancha cancha) {

        return new CanchaResponse(
                cancha.getId(),
                cancha.getNombre(),
                cancha.getTipo(),
                cancha.getPrecioHora(),
                cancha.isActiva());
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoCancha getTipo() {
        return tipo;
    }

    public BigDecimal getPrecioHora() {
        return precioHora;
    }

    public boolean isActiva() {
        return activa;
    }
}