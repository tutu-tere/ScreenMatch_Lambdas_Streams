package com.aluracursos.ScreenMatch.repository;

import com.aluracursos.ScreenMatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie,Long> {

   Optional <Serie> findByTituloContainsIgnoreCase(String nombreSerie);
}
