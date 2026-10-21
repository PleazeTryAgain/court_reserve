package com.courtreserve.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.courtreserve.exception.RecursoNoEncontradoException;
import com.courtreserve.model.Cancha;
import com.courtreserve.model.EstadoReserva;
import com.courtreserve.model.Reserva;
import com.courtreserve.model.TipoCancha;
import com.courtreserve.model.Usuario;
import com.courtreserve.service.CanchaService;
import com.courtreserve.service.ReservaService;
import com.courtreserve.service.UsuarioService;

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private CanchaService canchaService;

    @MockitoBean
    private ReservaService reservaService;

    private static Usuario usuario(Long id) {
        Usuario usuario = new Usuario("Carlos Perez", "carlos@email.com", "3001234567");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private static Cancha cancha(Long id, boolean activa) {
        Cancha cancha = new Cancha("Cancha Central", TipoCancha.FUTBOL, new BigDecimal("50000"), activa);
        ReflectionTestUtils.setField(cancha, "id", id);
        return cancha;
    }

    private static Reserva reserva(Long id, EstadoReserva estado) {
        Reserva reserva = new Reserva(
                usuario(1L),
                cancha(1L, true),
                LocalDate.now().plusDays(1),
                LocalTime.of(18, 0),
                LocalTime.of(19, 0),
                new BigDecimal("50000"),
                estado);
        ReflectionTestUtils.setField(reserva, "id", id);
        return reserva;
    }

    @Test
    void inicioMuestraLosTotales() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuario(1L), usuario(2L)));
        when(canchaService.listarCanchas()).thenReturn(List.of(cancha(1L, true)));
        when(reservaService.listarReservas()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("totalUsuarios", 2))
                .andExpect(model().attribute("totalCanchas", 1))
                .andExpect(model().attribute("totalReservas", 0));
    }

    @Test
    void usuariosMuestraElListado() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuario(1L)));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios"))
                .andExpect(model().attribute("usuarios", hasSize(1)));
    }

    @Test
    void crearUsuarioExitoso() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .param("nombre", "Carlos Perez")
                        .param("correo", "carlos@email.com")
                        .param("telefono", "3001234567"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attribute(WebController.MENSAJE_EXITO, "Usuario registrado correctamente."));

        verify(usuarioService).crearUsuario(any(Usuario.class));
    }

    @Test
    void crearUsuarioConCorreoDuplicadoMuestraError() throws Exception {
        when(usuarioService.crearUsuario(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un usuario con ese correo"));

        mockMvc.perform(post("/usuarios")
                        .param("nombre", "Carlos Perez")
                        .param("correo", "carlos@email.com"))
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attribute(WebController.MENSAJE_ERROR, "Ya existe un usuario con ese correo"));
    }

    @Test
    void canchasMuestraElListadoYLosTipos() throws Exception {
        when(canchaService.listarCanchas()).thenReturn(List.of(cancha(1L, true), cancha(2L, false)));

        mockMvc.perform(get("/canchas"))
                .andExpect(status().isOk())
                .andExpect(view().name("canchas"))
                .andExpect(model().attribute("canchas", hasSize(2)))
                .andExpect(model().attribute("tiposCancha", TipoCancha.values()));
    }

    @Test
    void crearCanchaExitosa() throws Exception {
        mockMvc.perform(post("/canchas")
                        .param("nombre", "Cancha Central")
                        .param("tipo", "FUTBOL")
                        .param("precioHora", "50000"))
                .andExpect(redirectedUrl("/canchas"))
                .andExpect(flash().attribute(WebController.MENSAJE_EXITO, "Cancha registrada correctamente."));

        verify(canchaService).crearCancha(any(Cancha.class));
    }

    @Test
    void crearCanchaConErrorMuestraMensaje() throws Exception {
        when(canchaService.crearCancha(any(Cancha.class)))
                .thenThrow(new IllegalArgumentException("Datos de cancha inválidos"));

        mockMvc.perform(post("/canchas")
                        .param("nombre", "Cancha Central")
                        .param("tipo", "TENIS")
                        .param("precioHora", "50000"))
                .andExpect(redirectedUrl("/canchas"))
                .andExpect(flash().attribute(WebController.MENSAJE_ERROR, "Datos de cancha inválidos"));
    }

    @Test
    void activarCancha() throws Exception {
        mockMvc.perform(post("/canchas/1/estado").param("activa", "true"))
                .andExpect(redirectedUrl("/canchas"))
                .andExpect(flash().attribute(WebController.MENSAJE_EXITO, "Cancha activada correctamente."));

        verify(canchaService).cambiarEstado(1L, true);
    }

    @Test
    void desactivarCancha() throws Exception {
        mockMvc.perform(post("/canchas/1/estado").param("activa", "false"))
                .andExpect(redirectedUrl("/canchas"))
                .andExpect(flash().attribute(WebController.MENSAJE_EXITO, "Cancha desactivada correctamente."));

        verify(canchaService).cambiarEstado(1L, false);
    }

    @Test
    void cambiarEstadoDeCanchaInexistenteMuestraError() throws Exception {
        when(canchaService.cambiarEstado(99L, false))
                .thenThrow(new RecursoNoEncontradoException("Cancha no encontrada"));

        mockMvc.perform(post("/canchas/99/estado").param("activa", "false"))
                .andExpect(redirectedUrl("/canchas"))
                .andExpect(flash().attribute(WebController.MENSAJE_ERROR, "Cancha no encontrada"));
    }

    @Test
    void reservasMuestraSoloCanchasActivas() throws Exception {
        when(reservaService.listarReservas()).thenReturn(List.of(
                reserva(1L, EstadoReserva.ACTIVA),
                reserva(2L, EstadoReserva.CANCELADA)));
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuario(1L)));
        when(canchaService.listarCanchas()).thenReturn(List.of(cancha(1L, true), cancha(2L, false)));

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservas"))
                .andExpect(model().attribute("reservas", hasSize(2)))
                .andExpect(model().attribute("usuarios", hasSize(1)))
                .andExpect(model().attribute("canchas", hasSize(1)))
                .andExpect(model().attribute("fechaMinima", LocalDate.now()));
    }

    @Test
    void crearReservaExitosa() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);

        mockMvc.perform(post("/reservas")
                        .param("usuarioId", "1")
                        .param("canchaId", "1")
                        .param("fecha", fecha.toString())
                        .param("horaInicio", "18:00")
                        .param("horaFin", "19:00"))
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attribute(WebController.MENSAJE_EXITO, "Reserva creada correctamente."));

        verify(reservaService).crearReserva(1L, 1L, fecha, LocalTime.of(18, 0), LocalTime.of(19, 0));
    }

    @Test
    void crearReservaSolapadaMuestraError() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        when(reservaService.crearReserva(1L, 1L, fecha, LocalTime.of(18, 0), LocalTime.of(19, 0)))
                .thenThrow(new IllegalStateException("La cancha ya está reservada en ese horario"));

        mockMvc.perform(post("/reservas")
                        .param("usuarioId", "1")
                        .param("canchaId", "1")
                        .param("fecha", fecha.toString())
                        .param("horaInicio", "18:00")
                        .param("horaFin", "19:00"))
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attribute(WebController.MENSAJE_ERROR, "La cancha ya está reservada en ese horario"));
    }

    @Test
    void cancelarReservaExitosa() throws Exception {
        mockMvc.perform(post("/reservas/1/cancelar"))
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attribute(WebController.MENSAJE_EXITO, "Reserva cancelada correctamente."));

        verify(reservaService).cancelarReserva(1L);
    }

    @Test
    void cancelarReservaYaCanceladaMuestraError() throws Exception {
        when(reservaService.cancelarReserva(1L))
                .thenThrow(new IllegalStateException("La reserva ya está cancelada"));

        mockMvc.perform(post("/reservas/1/cancelar"))
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attribute(WebController.MENSAJE_ERROR, "La reserva ya está cancelada"));
    }
}
