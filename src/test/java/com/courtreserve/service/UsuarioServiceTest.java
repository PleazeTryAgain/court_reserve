package com.courtreserve.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.courtreserve.model.Usuario;
import com.courtreserve.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deberiaCrearUsuarioCuandoCorreoNoExiste() {

        // ARRANGE
        Usuario usuario = new Usuario(
                "Carlos Perez",
                "carlos@email.com",
                "3001234567");

        when(usuarioRepository.existsByCorreo("carlos@email.com"))
                .thenReturn(false);

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        // ACT
        Usuario resultado = usuarioService.crearUsuario(usuario);

        // ASSERT
        assertEquals("Carlos Perez", resultado.getNombre());
        assertEquals("carlos@email.com", resultado.getCorreo());

        verify(usuarioRepository).existsByCorreo("carlos@email.com");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deberiaRechazarUsuarioCuandoCorreoYaExiste() {

        // ARRANGE
        Usuario usuario = new Usuario(
                "Carlos Perez",
                "carlos@email.com",
                "3001234567");

        when(usuarioRepository.existsByCorreo("carlos@email.com"))
                .thenReturn(true);

        // ACT
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.crearUsuario(usuario));

        // ASSERT
        assertEquals(
                "Ya existe un usuario con ese correo",
                excepcion.getMessage());

        verify(usuarioRepository).existsByCorreo("carlos@email.com");

        verify(usuarioRepository, never()).save(usuario);
    }
}