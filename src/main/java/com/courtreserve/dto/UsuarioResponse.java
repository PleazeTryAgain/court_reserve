package com.courtreserve.dto;

import com.courtreserve.model.Usuario;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private String telefono;

    public UsuarioResponse() {
    }

    public UsuarioResponse(
            Long id,
            String nombre,
            String correo,
            String telefono) {

        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
    }

    public static UsuarioResponse fromEntity(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono());
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }
}