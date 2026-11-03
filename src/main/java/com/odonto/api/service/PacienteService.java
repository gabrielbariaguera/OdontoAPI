package com.odonto.api.service;

import com.odonto.api.dto.PacienteRequestDTO;
import com.odonto.api.dto.PacienteResponseDTO;
import com.odonto.api.exception.BusinessException;
import com.odonto.api.exception.ResourceNotFoundException;
import com.odonto.api.model.Paciente;
import com.odonto.api.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> listarTodos(String nome) {
        List<Paciente> pacientes = (nome != null && !nome.isBlank())
                ? pacienteRepository.findByNomeContainingIgnoreCase(nome.trim())
                : pacienteRepository.findAll();

        return pacientes.stream()
                .map(PacienteResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPorId(Long id) {
        Paciente paciente = buscarEntidadePorId(id);
        return PacienteResponseDTO.fromEntity(paciente);
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPorCpf(String cpf) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o CPF: " + cpf));
        return PacienteResponseDTO.fromEntity(paciente);
    }

    @Transactional
    public PacienteResponseDTO criar(PacienteRequestDTO dto) {
        if (pacienteRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("Já existe um paciente cadastrado com o CPF: " + dto.cpf());
        }

        Paciente paciente = Paciente.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .dataNascimento(dto.dataNascimento())
                .observacoes(dto.observacoes())
                .build();

        Paciente salvo = pacienteRepository.save(paciente);
        return PacienteResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public PacienteResponseDTO atualizar(Long id, PacienteRequestDTO dto) {
        Paciente paciente = buscarEntidadePorId(id);

        if (pacienteRepository.existsByCpfAndIdNot(dto.cpf(), id)) {
            throw new BusinessException("Já existe outro paciente cadastrado com o CPF: " + dto.cpf());
        }

        paciente.setNome(dto.nome());
        paciente.setCpf(dto.cpf());
        paciente.setTelefone(dto.telefone());
        paciente.setEndereco(dto.endereco());
        paciente.setDataNascimento(dto.dataNascimento());
        paciente.setObservacoes(dto.observacoes());

        Paciente atualizado = pacienteRepository.save(paciente);
        return PacienteResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Paciente paciente = buscarEntidadePorId(id);
        pacienteRepository.delete(paciente);
    }

    private Paciente buscarEntidadePorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o ID: " + id));
    }
}
