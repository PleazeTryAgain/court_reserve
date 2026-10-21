package com.courtreserve.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.courtreserve.model.Cancha;
import com.courtreserve.model.TipoCancha;
import com.courtreserve.model.Usuario;
import com.courtreserve.service.CanchaService;
import com.courtreserve.service.ReservaService;
import com.courtreserve.service.UsuarioService;

@Controller
public class WebController {

    static final String MENSAJE_EXITO = "mensajeExito";
    static final String MENSAJE_ERROR = "mensajeError";

    private static final String VISTA_USUARIOS = "usuarios";
    private static final String VISTA_CANCHAS = "canchas";
    private static final String VISTA_RESERVAS = "reservas";

    private static final String REDIRECT = "redirect:/";

    private final UsuarioService usuarioService;
    private final CanchaService canchaService;
    private final ReservaService reservaService;

    public WebController(
            UsuarioService usuarioService,
            CanchaService canchaService,
            ReservaService reservaService) {

        this.usuarioService = usuarioService;
        this.canchaService = canchaService;
        this.reservaService = reservaService;
    }

    @GetMapping("/")
    public String inicio(Model model) {

        model.addAttribute(
                "totalUsuarios",
                usuarioService.listarUsuarios().size());

        model.addAttribute(
                "totalCanchas",
                canchaService.listarCanchas().size());

        model.addAttribute(
                "totalReservas",
                reservaService.listarReservas().size());

        return "index";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {

        model.addAttribute(
                VISTA_USUARIOS,
                usuarioService.listarUsuarios());

        return VISTA_USUARIOS;
    }

    @PostMapping("/usuarios")
    public String crearUsuarioWeb(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes) {

        ejecutarConMensaje(
                () -> usuarioService.crearUsuario(new Usuario(nombre, correo, telefono)),
                "Usuario registrado correctamente.",
                redirectAttributes);

        return REDIRECT + VISTA_USUARIOS;
    }

    @GetMapping("/canchas")
    public String canchas(Model model) {

        model.addAttribute(
                VISTA_CANCHAS,
                canchaService.listarCanchas());

        model.addAttribute(
                "tiposCancha",
                TipoCancha.values());

        return VISTA_CANCHAS;
    }

    @PostMapping("/canchas")
    public String crearCanchaWeb(
            @RequestParam String nombre,
            @RequestParam TipoCancha tipo,
            @RequestParam BigDecimal precioHora,
            RedirectAttributes redirectAttributes) {

        ejecutarConMensaje(
                () -> canchaService.crearCancha(new Cancha(nombre, tipo, precioHora, true)),
                "Cancha registrada correctamente.",
                redirectAttributes);

        return REDIRECT + VISTA_CANCHAS;
    }

    @PostMapping("/canchas/{id}/estado")
    public String cambiarEstadoCanchaWeb(
            @PathVariable Long id,
            @RequestParam boolean activa,
            RedirectAttributes redirectAttributes) {

        ejecutarConMensaje(
                () -> canchaService.cambiarEstado(id, activa),
                activa
                        ? "Cancha activada correctamente."
                        : "Cancha desactivada correctamente.",
                redirectAttributes);

        return REDIRECT + VISTA_CANCHAS;
    }

    @GetMapping("/reservas")
    public String reservas(Model model) {

        model.addAttribute(
                VISTA_RESERVAS,
                reservaService.listarReservas());

        model.addAttribute(
                VISTA_USUARIOS,
                usuarioService.listarUsuarios());

        model.addAttribute(
                VISTA_CANCHAS,
                canchaService.listarCanchas()
                        .stream()
                        .filter(Cancha::isActiva)
                        .toList());

        model.addAttribute(
                "fechaMinima",
                LocalDate.now());

        return VISTA_RESERVAS;
    }

    @PostMapping("/reservas")
    public String crearReservaWeb(
            @RequestParam Long usuarioId,
            @RequestParam Long canchaId,
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime horaInicio,
            @RequestParam LocalTime horaFin,
            RedirectAttributes redirectAttributes) {

        ejecutarConMensaje(
                () -> reservaService.crearReserva(
                        usuarioId,
                        canchaId,
                        fecha,
                        horaInicio,
                        horaFin),
                "Reserva creada correctamente.",
                redirectAttributes);

        return REDIRECT + VISTA_RESERVAS;
    }

    @PostMapping("/reservas/{id}/cancelar")
    public String cancelarReservaWeb(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        ejecutarConMensaje(
                () -> reservaService.cancelarReserva(id),
                "Reserva cancelada correctamente.",
                redirectAttributes);

        return REDIRECT + VISTA_RESERVAS;
    }

    /**
     * Ejecuta una operación de negocio y deja en la redirección un mensaje
     * de éxito o el mensaje de la regla de negocio que se incumplió.
     */
    private void ejecutarConMensaje(
            Runnable operacion,
            String mensajeExito,
            RedirectAttributes redirectAttributes) {

        try {

            operacion.run();

            redirectAttributes.addFlashAttribute(MENSAJE_EXITO, mensajeExito);

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(MENSAJE_ERROR, e.getMessage());
        }
    }
}
