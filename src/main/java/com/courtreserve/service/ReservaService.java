package com.courtreserve.service;

import java.math.RoundingMode;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courtreserve.exception.RecursoNoEncontradoException;
import com.courtreserve.model.Cancha;
import com.courtreserve.model.EstadoReserva;
import com.courtreserve.model.Reserva;
import com.courtreserve.model.Usuario;
import com.courtreserve.repository.CanchaRepository;
import com.courtreserve.repository.ReservaRepository;
import com.courtreserve.repository.UsuarioRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CanchaRepository canchaRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            CanchaRepository canchaRepository) {

        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.canchaRepository = canchaRepository;
    }

    public Reserva crearReserva(
            Long usuarioId,
            Long canchaId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin) {

        validarFecha(fecha);
        validarHorario(horaInicio, horaFin);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Cancha cancha = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cancha no encontrada"));

        if (!cancha.isActiva()) {
            throw new IllegalStateException("La cancha no está disponible");
        }

        boolean existeSolapamiento = reservaRepository.existeReservaSolapada(
                canchaId,
                fecha,
                horaInicio,
                horaFin,
                EstadoReserva.ACTIVA);

        if (existeSolapamiento) {
            throw new IllegalStateException("La cancha ya está reservada en ese horario");
        }

        BigDecimal precioTotal = calcularPrecio(
                cancha.getPrecioHora(),
                horaInicio,
                horaFin);

        Reserva reserva = new Reserva(
                usuario,
                cancha,
                fecha,
                horaInicio,
                horaFin,
                precioTotal,
                EstadoReserva.ACTIVA);

        return reservaRepository.save(reserva);
    }

    public void validarFecha(LocalDate fecha) {

        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede reservar en una fecha pasada");
        }
    }

    public void validarHorario(LocalTime horaInicio, LocalTime horaFin) {

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                    "La hora final debe ser posterior a la hora inicial");
        }

        long minutos = Duration.between(horaInicio, horaFin).toMinutes();

        if (minutos > 180) {
            throw new IllegalArgumentException(
                    "La reserva no puede superar las 3 horas");
        }

        if (minutos < 60) {
            throw new IllegalArgumentException(
                    "La reserva debe durar al menos 1 hora");
        }
    }

    public BigDecimal calcularPrecio(
            BigDecimal precioHora,
            LocalTime horaInicio,
            LocalTime horaFin) {

        long minutos = Duration.between(horaInicio, horaFin).toMinutes();

        return precioHora
                .multiply(BigDecimal.valueOf(minutos))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    @Transactional
    public Reserva cancelarReserva(Long reservaId) {

        Reserva reserva = reservaRepository.findByIdConRelaciones(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva ya está cancelada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);

        return reserva;
    }

    public List<Reserva> listarReservas() {
        return reservaRepository.findAllConRelaciones();
    }
}