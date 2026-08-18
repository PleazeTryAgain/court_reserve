package com.courtreserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.courtreserve.model.Cancha;

public interface CanchaRepository extends JpaRepository<Cancha, Long> {
}