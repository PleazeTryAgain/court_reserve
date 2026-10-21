package com.courtreserve.exception;

/**
 * Se lanza cuando no existe el recurso solicitado (usuario, cancha o reserva).
 * Se traduce a una respuesta HTTP 404.
 */
public class RecursoNoEncontradoException extends IllegalArgumentException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
