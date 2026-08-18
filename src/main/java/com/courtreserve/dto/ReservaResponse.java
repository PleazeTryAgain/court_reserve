package com.courtreserve.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.courtreserve.model.EstadoReserva;
import com.courtreserve.model.Reserva;

public class ReservaResponse {

    private Long id;

    private Long usuarioId;
    private String usuarioNombre;

    private Long canchaId;
    private String canchaNombre;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private BigDecimal precioTotal;
    private EstadoReserva estado;

    public ReservaResponse() {
    }

    public ReservaResponse(
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

        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.canchaId = canchaId;
        this.canchaNombre = canchaNombre;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.precioTotal = precioTotal;
        this.estado = estado;
    }

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

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public Long getCanchaId() {
        return canchaId;
    }

    public String getCanchaNombre() {
        return canchaNombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}