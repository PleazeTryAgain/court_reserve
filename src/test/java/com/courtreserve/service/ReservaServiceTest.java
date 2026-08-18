package com.courtreserve.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.courtreserve.model.Cancha;
import com.courtreserve.model.EstadoReserva;
import com.courtreserve.model.Reserva;
import com.courtreserve.model.TipoCancha;
import com.courtreserve.model.Usuario;
import com.courtreserve.repository.CanchaRepository;
import com.courtreserve.repository.ReservaRepository;
import com.courtreserve.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CanchaRepository canchaRepository;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void deberiaRechazarReservaEnFechaPasada() {

        // ARRANGE
        LocalDate fechaPasada = LocalDate.now().minusDays(1);

        // ACT
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.validarFecha(fechaPasada));

        // ASSERT
        assertEquals(
                "No se puede reservar en una fecha pasada",
                excepcion.getMessage());
    }

    @Test
    void deberiaRechazarReservaMayorATresHoras() {

        // ARRANGE
        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaFin = LocalTime.of(12, 0);

        // ACT
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.validarHorario(horaInicio, horaFin));

        // ASSERT
        assertEquals(
                "La reserva no puede superar las 3 horas",
                excepcion.getMessage());
    }

    @Test
    void deberiaCalcularCorrectamenteElPrecio() {

        // ARRANGE
        BigDecimal precioHora = new BigDecimal("50000");
        LocalTime horaInicio = LocalTime.of(18, 0);
        LocalTime horaFin = LocalTime.of(20, 0);

        // ACT
        BigDecimal resultado = reservaService.calcularPrecio(
                precioHora,
                horaInicio,
                horaFin);

        // ASSERT
        assertEquals(
                new BigDecimal("100000.00"),
                resultado);
    }

    @Test
    void deberiaRechazarReservaCuandoCanchaEstaInactiva() {

        // ARRANGE
        Long usuarioId = 1L;
        Long canchaId = 1L;

        Usuario usuario = new Usuario(
                "Carlos Perez",
                "carlos@email.com",
                "3001234567");

        Cancha cancha = new Cancha(
                "Cancha 1",
                TipoCancha.FUTBOL,
                new BigDecimal("50000"),
                false);

        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime horaInicio = LocalTime.of(18, 0);
        LocalTime horaFin = LocalTime.of(20, 0);

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(canchaRepository.findById(canchaId))
                .thenReturn(Optional.of(cancha));

        // ACT
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> reservaService.crearReserva(
                        usuarioId,
                        canchaId,
                        fecha,
                        horaInicio,
                        horaFin));

        // ASSERT
        assertEquals(
                "La cancha no está disponible",
                excepcion.getMessage());

        verify(reservaRepository, never())
                .save(any(Reserva.class));
    }

    @Test
    void deberiaRechazarReservaCuandoExisteSolapamiento() {

        // ARRANGE
        Long usuarioId = 1L;
        Long canchaId = 1L;

        Usuario usuario = new Usuario(
                "Carlos Perez",
                "carlos@email.com",
                "3001234567");

        Cancha cancha = new Cancha(
                "Cancha 1",
                TipoCancha.FUTBOL,
                new BigDecimal("50000"),
                true);

        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime horaInicio = LocalTime.of(19, 0);
        LocalTime horaFin = LocalTime.of(20, 0);

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(canchaRepository.findById(canchaId))
                .thenReturn(Optional.of(cancha));

        when(reservaRepository.existeReservaSolapada(
                canchaId,
                fecha,
                horaInicio,
                horaFin,
                EstadoReserva.ACTIVA))
                .thenReturn(true);

        // ACT
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> reservaService.crearReserva(
                        usuarioId,
                        canchaId,
                        fecha,
                        horaInicio,
                        horaFin));

        // ASSERT
        assertEquals(
                "La cancha ya está reservada en ese horario",
                excepcion.getMessage());

        verify(reservaRepository, never())
                .save(any(Reserva.class));
    }

    @Test
    void deberiaCrearReservaCuandoDatosSonValidos() {

        // ARRANGE
        Long usuarioId = 1L;
        Long canchaId = 1L;

        Usuario usuario = new Usuario(
                "Carlos Perez",
                "carlos@email.com",
                "3001234567");

        Cancha cancha = new Cancha(
                "Cancha 1",
                TipoCancha.FUTBOL,
                new BigDecimal("50000"),
                true);

        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime horaInicio = LocalTime.of(18, 0);
        LocalTime horaFin = LocalTime.of(20, 0);

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(canchaRepository.findById(canchaId))
                .thenReturn(Optional.of(cancha));

        when(reservaRepository.existeReservaSolapada(
                canchaId,
                fecha,
                horaInicio,
                horaFin,
                EstadoReserva.ACTIVA))
                .thenReturn(false);

        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // ACT
        Reserva resultado = reservaService.crearReserva(
                usuarioId,
                canchaId,
                fecha,
                horaInicio,
                horaFin);

        // ASSERT
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(cancha, resultado.getCancha());
        assertEquals(fecha, resultado.getFecha());
        assertEquals(horaInicio, resultado.getHoraInicio());
        assertEquals(horaFin, resultado.getHoraFin());

        assertEquals(
                new BigDecimal("100000.00"),
                resultado.getPrecioTotal());

        assertEquals(
                EstadoReserva.ACTIVA,
                resultado.getEstado());

        assertTrue(resultado.getCancha().isActiva());

        verify(reservaRepository)
                .save(any(Reserva.class));
    }

    @Test
    void deberiaRechazarReservaMenorAUnaHora() {

        // ARRANGE
        LocalTime horaInicio = LocalTime.of(18, 0);
        LocalTime horaFin = LocalTime.of(18, 30);

        // ACT
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.validarHorario(horaInicio, horaFin));

        // ASSERT
        assertEquals(
                "La reserva debe durar al menos 1 hora",
                excepcion.getMessage());
    }

    @Test
    void deberiaRechazarHorarioCuandoHoraFinEsIgualAHoraInicio() {

        // ARRANGE
        LocalTime horaInicio = LocalTime.of(18, 0);
        LocalTime horaFin = LocalTime.of(18, 0);

        // ACT
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.validarHorario(horaInicio, horaFin));

        // ASSERT
        assertEquals(
                "La hora final debe ser posterior a la hora inicial",
                excepcion.getMessage());
    }

    @Test
    void deberiaPermitirReservaDeExactamenteUnaHora() {

        // ARRANGE
        LocalTime horaInicio = LocalTime.of(18, 0);
        LocalTime horaFin = LocalTime.of(19, 0);

        // ACT
        reservaService.validarHorario(horaInicio, horaFin);

        // ASSERT
        // Si no se lanza ninguna excepción, el horario es válido.
    }

    @Test
    void deberiaCalcularPrecioDeUnaHoraYMedia() {

        // ARRANGE
        BigDecimal precioHora = new BigDecimal("50000");
        LocalTime horaInicio = LocalTime.of(18, 0);
        LocalTime horaFin = LocalTime.of(19, 30);

        // ACT
        BigDecimal resultado = reservaService.calcularPrecio(
                precioHora,
                horaInicio,
                horaFin);

        // ASSERT
        assertEquals(
                new BigDecimal("75000.00"),
                resultado);
    }
}