package com.odonto.api.service;

import com.odonto.api.dto.ConsultaRequestDTO;
import com.odonto.api.dto.ConsultaResponseDTO;
import com.odonto.api.exception.ResourceNotFoundException;
import com.odonto.api.model.Consulta;
import com.odonto.api.model.Paciente;
import com.odonto.api.repository.ConsultaRepository;
import com.odonto.api.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarTodas(
            Long pacienteId,
            LocalDate data,
            String status,
            String tipoConsulta
    ) {
        List<Consulta> consultas = consultaRepository.findAll();

        return consultas.stream()
                .filter(c -> pacienteId == null || c.getPaciente().getId().equals(pacienteId))
                .filter(c -> data == null || c.getData().equals(data))
                .filter(c -> status == null || status.isBlank() || c.getStatus().equalsIgnoreCase(status.trim()))
                .filter(c -> tipoConsulta == null || tipoConsulta.isBlank() || c.getTipoConsulta().equalsIgnoreCase(tipoConsulta.trim()))
                .map(ConsultaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarPorId(Long id) {
        Consulta consulta = buscarEntidadePorId(id);
        return ConsultaResponseDTO.fromEntity(consulta);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarPorPaciente(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente não encontrado com o ID: " + pacienteId);
        }
        return consultaRepository.findByPacienteIdOrderByDataDescHoraDesc(pacienteId)
                .stream()
                .map(ConsultaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public ConsultaResponseDTO criar(ConsultaRequestDTO dto) {
        Paciente paciente = buscarPacientePorId(dto.pacienteId());

        Consulta consulta = Consulta.builder()
                .paciente(paciente)
                .data(dto.data())
                .hora(dto.hora())
                .procedimento(dto.procedimento())
                .status(dto.status())
                .tipoConsulta(dto.tipoConsulta())
                .build();

        paciente.adicionarConsulta(consulta);
        Consulta salva = consultaRepository.save(consulta);

        return ConsultaResponseDTO.fromEntity(salva);
    }

    @Transactional
    public ConsultaResponseDTO atualizar(Long id, ConsultaRequestDTO dto) {
        Consulta consulta = buscarEntidadePorId(id);

        if (!consulta.getPaciente().getId().equals(dto.pacienteId())) {
            Paciente pacienteAntigo = consulta.getPaciente();
            Paciente novoPaciente = buscarPacientePorId(dto.pacienteId());
            pacienteAntigo.removerConsulta(consulta);
            novoPaciente.adicionarConsulta(consulta);
        }

        consulta.setData(dto.data());
        consulta.setHora(dto.hora());
        consulta.setProcedimento(dto.procedimento());
        consulta.setStatus(dto.status());
        consulta.setTipoConsulta(dto.tipoConsulta());

        Consulta atualizada = consultaRepository.save(consulta);
        return ConsultaResponseDTO.fromEntity(atualizada);
    }

    @Transactional
    public ConsultaResponseDTO atualizarStatus(Long id, String status) {
        Consulta consulta = buscarEntidadePorId(id);
        consulta.setStatus(status);
        Consulta atualizada = consultaRepository.save(consulta);
        return ConsultaResponseDTO.fromEntity(atualizada);
    }

    @Transactional
    public void deletar(Long id) {
        Consulta consulta = buscarEntidadePorId(id);
        Paciente paciente = consulta.getPaciente();
        if (paciente != null) {
            paciente.removerConsulta(consulta);
        }
        consultaRepository.delete(consulta);
    }

    private Consulta buscarEntidadePorId(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com o ID: " + id));
    }

    private Paciente buscarPacientePorId(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o ID: " + pacienteId));
    }
}
