package com.courtreserve.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.courtreserve.model.Cancha;
import com.courtreserve.repository.CanchaRepository;

@Service
public class CanchaService {

    private final CanchaRepository canchaRepository;

    public CanchaService(CanchaRepository canchaRepository) {
        this.canchaRepository = canchaRepository;
    }

    public Cancha crearCancha(Cancha cancha) {
        return canchaRepository.save(cancha);
    }

    public List<Cancha> listarCanchas() {
        return canchaRepository.findAll();
    }

    public Cancha buscarPorId(Long id) {
        return canchaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cancha no encontrada"));
    }

    public Cancha cambiarEstado(Long id, boolean activa) {
        Cancha cancha = buscarPorId(id);
        cancha.setActiva(activa);
        return canchaRepository.save(cancha);
    }
}