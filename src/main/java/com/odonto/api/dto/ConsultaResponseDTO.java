package com.odonto.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.odonto.api.model.Consulta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Dados completos de retorno de uma consulta odontológica")
public record ConsultaResponseDTO(
        @Schema(description = "ID único da consulta", example = "10")
        Long id,

        @Schema(description = "ID do paciente vinculado à consulta", example = "1")
        Long pacienteId,

        @Schema(description = "Nome do paciente", example = "Maria Silva")
        String pacienteNome,

        @Schema(description = "CPF do paciente", example = "123.456.789-00")
        String pacienteCpf,

        @Schema(description = "Data da consulta", example = "2026-11-15")
        LocalDate data,

        @Schema(description = "Hora da consulta (HH:mm)", example = "14:30", type = "string")
        @JsonFormat(pattern = "HH:mm")
        LocalTime hora,

        @Schema(description = "Procedimento odontológico", example = "Limpeza e Profilaxia")
        String procedimento,

        @Schema(description = "Status atual da consulta", example = "AGENDADA")
        String status,

        @Schema(description = "Tipo de consulta", example = "Rotina")
        String tipoConsulta
) {
    public static ConsultaResponseDTO fromEntity(Consulta consulta) {
        return new ConsultaResponseDTO(
                consulta.getId(),
                consulta.getPaciente().getId(),
                consulta.getPaciente().getNome(),
                consulta.getPaciente().getCpf(),
                consulta.getData(),
                consulta.getHora(),
                consulta.getProcedimento(),
                consulta.getStatus(),
                consulta.getTipoConsulta()
        );
    }
}
