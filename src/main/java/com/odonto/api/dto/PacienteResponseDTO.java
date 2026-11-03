package com.odonto.api.dto;

import com.odonto.api.model.Paciente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Schema(description = "Dados de retorno de um paciente cadastrado, incluindo idade calculada e histórico de consultas")
public record PacienteResponseDTO(
        @Schema(description = "ID único do paciente", example = "1")
        Long id,

        @Schema(description = "Nome completo do paciente", example = "Maria Silva")
        String nome,

        @Schema(description = "CPF do paciente", example = "123.456.789-00")
        String cpf,

        @Schema(description = "Telefone de contato", example = "(11) 98765-4321")
        String telefone,

        @Schema(description = "Endereço completo do paciente", example = "Rua das Flores, 123 - Centro, São Paulo - SP")
        String endereco,

        @Schema(description = "Data de nascimento do paciente", example = "1995-05-20")
        LocalDate dataNascimento,

        @Schema(description = "Idade atual do paciente calculada automaticamente", example = "31")
        Integer idade,

        @Schema(description = "Observações clínicas ou gerais do paciente", example = "Paciente alérgico a dipirona.")
        String observacoes,

        @Schema(description = "Histórico de todas as consultas do paciente")
        List<ConsultaResumoDTO> consultas
) {
    public static PacienteResponseDTO fromEntity(Paciente paciente) {
        List<ConsultaResumoDTO> consultasDTO = paciente.getConsultas() != null
                ? paciente.getConsultas().stream().map(ConsultaResumoDTO::fromEntity).toList()
                : Collections.emptyList();

        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getCpf(),
                paciente.getTelefone(),
                paciente.getEndereco(),
                paciente.getDataNascimento(),
                paciente.getIdade(),
                paciente.getObservacoes(),
                consultasDTO
        );
    }
}
