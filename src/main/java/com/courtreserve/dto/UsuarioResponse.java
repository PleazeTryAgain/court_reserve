package com.courtreserve.dto;

import com.courtreserve.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String telefono) {

    public static UsuarioResponse fromEntity(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono());
    }
}
