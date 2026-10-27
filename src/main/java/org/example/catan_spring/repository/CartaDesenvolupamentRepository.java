package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.CartaDesenvolupament;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaDesenvolupamentRepository extends JpaRepository<CartaDesenvolupament, Integer> {
    Optional<CartaDesenvolupament> findById(Integer id);
}
