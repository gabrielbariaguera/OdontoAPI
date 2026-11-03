package com.odonto.api.repository;

import com.odonto.api.model.Paciente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    @Override
    @EntityGraph(attributePaths = "consultas")
    List<Paciente> findAll();

    @Override
    @EntityGraph(attributePaths = "consultas")
    Optional<Paciente> findById(Long id);

    @EntityGraph(attributePaths = "consultas")
    Optional<Paciente> findByCpf(String cpf);

    @EntityGraph(attributePaths = "consultas")
    List<Paciente> findByNomeContainingIgnoreCase(String nome);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);
}
