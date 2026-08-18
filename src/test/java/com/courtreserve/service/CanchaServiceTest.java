package com.courtreserve.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.courtreserve.model.Cancha;
import com.courtreserve.model.TipoCancha;
import com.courtreserve.repository.CanchaRepository;

@ExtendWith(MockitoExtension.class)
class CanchaServiceTest {

    @Mock
    private CanchaRepository canchaRepository;

    @InjectMocks
    private CanchaService canchaService;

    @Test
    void deberiaCrearCanchaCorrectamente() {

        // ARRANGE
        Cancha cancha = new Cancha(
                "Cancha Central",
                TipoCancha.FUTBOL,
                new BigDecimal("50000"),
                true);

        when(canchaRepository.save(cancha))
                .thenReturn(cancha);

        // ACT
        Cancha resultado = canchaService.crearCancha(cancha);

        // ASSERT
        assertEquals("Cancha Central", resultado.getNombre());
        assertEquals(TipoCancha.FUTBOL, resultado.getTipo());
        assertEquals(new BigDecimal("50000"), resultado.getPrecioHora());
        assertTrue(resultado.isActiva());

        verify(canchaRepository).save(cancha);
    }

    @Test
    void deberiaListarTodasLasCanchas() {

        // ARRANGE
        Cancha cancha1 = new Cancha(
                "Cancha 1",
                TipoCancha.FUTBOL,
                new BigDecimal("50000"),
                true);

        Cancha cancha2 = new Cancha(
                "Cancha 2",
                TipoCancha.TENIS,
                new BigDecimal("30000"),
                true);

        List<Cancha> canchas = List.of(cancha1, cancha2);

        when(canchaRepository.findAll())
                .thenReturn(canchas);

        // ACT
        List<Cancha> resultado = canchaService.listarCanchas();

        // ASSERT
        assertEquals(2, resultado.size());
        assertEquals("Cancha 1", resultado.get(0).getNombre());
        assertEquals("Cancha 2", resultado.get(1).getNombre());

        verify(canchaRepository).findAll();
    }

    @Test
    void deberiaBuscarCanchaPorIdCuandoExiste() {

        // ARRANGE
        Long canchaId = 1L;

        Cancha cancha = new Cancha(
                "Cancha Central",
                TipoCancha.FUTBOL,
                new BigDecimal("50000"),
                true);

        when(canchaRepository.findById(canchaId))
                .thenReturn(Optional.of(cancha));

        // ACT
        Cancha resultado = canchaService.buscarPorId(canchaId);

        // ASSERT
        assertEquals("Cancha Central", resultado.getNombre());
        assertEquals(TipoCancha.FUTBOL, resultado.getTipo());

        verify(canchaRepository).findById(canchaId);
    }

    @Test
    void deberiaLanzarExcepcionCuandoCanchaNoExiste() {

        // ARRANGE
        Long canchaId = 99L;

        when(canchaRepository.findById(canchaId))
                .thenReturn(Optional.empty());

        // ACT
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> canchaService.buscarPorId(canchaId));

        // ASSERT
        assertEquals(
                "Cancha no encontrada",
                excepcion.getMessage());

        verify(canchaRepository).findById(canchaId);
    }

    @Test
    void deberiaDesactivarUnaCancha() {

        // ARRANGE
        Long canchaId = 1L;

        Cancha cancha = new Cancha(
                "Cancha Central",
                TipoCancha.FUTBOL,
                new BigDecimal("50000"),
                true);

        when(canchaRepository.findById(canchaId))
                .thenReturn(Optional.of(cancha));

        when(canchaRepository.save(cancha))
                .thenReturn(cancha);

        // ACT
        Cancha resultado = canchaService.cambiarEstado(
                canchaId,
                false);

        // ASSERT
        assertFalse(resultado.isActiva());

        verify(canchaRepository).findById(canchaId);
        verify(canchaRepository).save(cancha);
    }

    @Test
    void deberiaActivarUnaCancha() {

        // ARRANGE
        Long canchaId = 1L;

        Cancha cancha = new Cancha(
                "Cancha Central",
                TipoCancha.FUTBOL,
                new BigDecimal("50000"),
                false);

        when(canchaRepository.findById(canchaId))
                .thenReturn(Optional.of(cancha));

        when(canchaRepository.save(cancha))
                .thenReturn(cancha);

        // ACT
        Cancha resultado = canchaService.cambiarEstado(
                canchaId,
                true);

        // ASSERT
        assertTrue(resultado.isActiva());

        verify(canchaRepository).findById(canchaId);
        verify(canchaRepository).save(cancha);
    }
}