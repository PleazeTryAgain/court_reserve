package com.courtreserve.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.courtreserve.model.EstadoReserva;
import com.courtreserve.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByCanchaId(Long canchaId);

    @Query("""
        SELECT r
        FROM Reserva r
        JOIN FETCH r.usuario
        JOIN FETCH r.cancha
        WHERE r.id = :id
        """)
    Optional<Reserva> findByIdConRelaciones(@Param("id") Long id);

    @Query("""
        SELECT r
        FROM Reserva r
        JOIN FETCH r.usuario
        JOIN FETCH r.cancha
        ORDER BY r.fecha, r.horaInicio
        """)
    List<Reserva> findAllConRelaciones();

    @Query("""
        SELECT COUNT(r) > 0
        FROM Reserva r
        WHERE r.cancha.id = :canchaId
          AND r.fecha = :fecha
          AND r.estado = :estado
          AND r.horaInicio < :horaFin
          AND r.horaFin > :horaInicio
        """)
    boolean existeReservaSolapada(
            @Param("canchaId") Long canchaId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estado") EstadoReserva estado);
}