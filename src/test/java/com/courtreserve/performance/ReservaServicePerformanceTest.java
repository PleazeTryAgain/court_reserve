package com.courtreserve.performance;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
import com.courtreserve.service.ReservaService;

@ExtendWith(MockitoExtension.class)
class ReservaServicePerformanceTest {

        @Mock
        private ReservaRepository reservaRepository;

        @Mock
        private UsuarioRepository usuarioRepository;

        @Mock
        private CanchaRepository canchaRepository;

        @InjectMocks
        private ReservaService reservaService;

        @Test
        void deberiaCrearReservaEnMenosDe100Milisegundos() {

                // ARRANGE
                Long usuarioId = 1L;
                Long canchaId = 1L;

                Usuario usuario = new Usuario(
                                "Carlos Perez",
                                "carlos@email.com",
                                "3001234567");

                Cancha cancha = new Cancha(
                                "Cancha Central",
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

                // ACT + ASSERT
                assertTimeout(
                                Duration.ofMillis(100),
                                () -> reservaService.crearReserva(
                                                usuarioId,
                                                canchaId,
                                                fecha,
                                                horaInicio,
                                                horaFin));
        }

        @Test
        void deberiaCalcularPrecioEnMenosDe20Milisegundos() {

                // ARRANGE
                BigDecimal precioHora = new BigDecimal("50000");
                LocalTime horaInicio = LocalTime.of(18, 0);
                LocalTime horaFin = LocalTime.of(19, 30);

                // ACT + ASSERT
                assertTimeout(
                                Duration.ofMillis(20),
                                () -> reservaService.calcularPrecio(
                                                precioHora,
                                                horaInicio,
                                                horaFin));
        }

        @Test
        void deberiaListarDiezMilReservasEnMenosDe100Milisegundos() {

                // ARRANGE
                List<Reserva> reservas = new ArrayList<>();

                for (int i = 0; i < 10000; i++) {
                        reservas.add(new Reserva());
                }

                when(reservaRepository.findAllConRelaciones())
                                .thenReturn(reservas);

                // ACT + ASSERT
                assertTimeout(
                                Duration.ofMillis(100),
                                () -> reservaService.listarReservas());
        }
}