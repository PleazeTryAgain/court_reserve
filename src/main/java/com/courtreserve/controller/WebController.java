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
                "usuarios",
                usuarioService.listarUsuarios());

        return "usuarios";
    }

    @PostMapping("/usuarios")
    public String crearUsuarioWeb(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes) {

        try {

            Usuario usuario = new Usuario(
                    nombre,
                    correo,
                    telefono);

            usuarioService.crearUsuario(usuario);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Usuario registrado correctamente.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage());
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/canchas")
    public String canchas(Model model) {

        model.addAttribute(
                "canchas",
                canchaService.listarCanchas());

        model.addAttribute(
                "tiposCancha",
                TipoCancha.values());

        return "canchas";
    }

    @PostMapping("/canchas")
    public String crearCanchaWeb(
            @RequestParam String nombre,
            @RequestParam TipoCancha tipo,
            @RequestParam BigDecimal precioHora,
            RedirectAttributes redirectAttributes) {

        try {

            Cancha cancha = new Cancha(
                    nombre,
                    tipo,
                    precioHora,
                    true);

            canchaService.crearCancha(cancha);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Cancha registrada correctamente.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage());
        }

        return "redirect:/canchas";
    }

    @PostMapping("/canchas/{id}/estado")
    public String cambiarEstadoCanchaWeb(
            @PathVariable Long id,
            @RequestParam boolean activa,
            RedirectAttributes redirectAttributes) {

        try {

            canchaService.cambiarEstado(id, activa);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    activa
                            ? "Cancha activada correctamente."
                            : "Cancha desactivada correctamente.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage());
        }

        return "redirect:/canchas";
    }

    @GetMapping("/reservas")
    public String reservas(Model model) {

        model.addAttribute(
                "reservas",
                reservaService.listarReservas());

        model.addAttribute(
                "usuarios",
                usuarioService.listarUsuarios());

        model.addAttribute(
                "canchas",
                canchaService.listarCanchas()
                        .stream()
                        .filter(Cancha::isActiva)
                        .toList());

        model.addAttribute(
                "fechaMinima",
                LocalDate.now());

        return "reservas";
    }

    @PostMapping("/reservas")
    public String crearReservaWeb(
            @RequestParam Long usuarioId,
            @RequestParam Long canchaId,
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime horaInicio,
            @RequestParam LocalTime horaFin,
            RedirectAttributes redirectAttributes) {

        try {

            reservaService.crearReserva(
                    usuarioId,
                    canchaId,
                    fecha,
                    horaInicio,
                    horaFin);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Reserva creada correctamente.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage());
        }

        return "redirect:/reservas";
    }

    @PostMapping("/reservas/{id}/cancelar")
    public String cancelarReservaWeb(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            reservaService.cancelarReserva(id);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Reserva cancelada correctamente.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage());
        }

        return "redirect:/reservas";
    }
}