package com.odonto.api.repository;

import com.odonto.api.model.Consulta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Override
    @EntityGraph(attributePaths = "paciente")
    List<Consulta> findAll();

    @Override
    @EntityGraph(attributePaths = "paciente")
    Optional<Consulta> findById(Long id);

    @EntityGraph(attributePaths = "paciente")
    List<Consulta> findByPacienteIdOrderByDataDescHoraDesc(Long pacienteId);

    @EntityGraph(attributePaths = "paciente")
    List<Consulta> findByDataOrderByHoraAsc(LocalDate data);

    @EntityGraph(attributePaths = "paciente")
    List<Consulta> findByStatusIgnoreCaseOrderByDataDescHoraDesc(String status);

    @EntityGraph(attributePaths = "paciente")
    List<Consulta> findByTipoConsultaIgnoreCaseOrderByDataDescHoraDesc(String tipoConsulta);
}
