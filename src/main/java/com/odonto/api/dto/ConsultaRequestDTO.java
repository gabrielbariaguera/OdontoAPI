package com.odonto.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaRequestDTO(

        @Schema(description = "ID do paciente que realizará a consulta", example = "1")
        @NotNull(message = "O paciente (pacienteId) é obrigatório")
        Long pacienteId,

        @Schema(description = "Data da consulta", example = "2026-11-15")
        @NotNull(message = "A data da consulta é obrigatória")
        LocalDate data,

        @Schema(description = "Hora da consulta (HH:mm)", example = "14:30", type = "string")
        @NotNull(message = "A hora da consulta é obrigatória")
        LocalTime hora,

        @Schema(description = "Procedimento odontológico a ser realizado ou realizado", example = "Limpeza e Profilaxia")
        @NotBlank(message = "O procedimento é obrigatório")
        @Size(max = 200, message = "O procedimento deve ter no máximo 200 caracteres")
        String procedimento,

        @Schema(description = "Status da consulta (ex: AGENDADA, CONFIRMADA, CONCLUIDA, CANCELADA)", example = "AGENDADA")
        @NotBlank(message = "O status da consulta é obrigatório")
        @Size(max = 50, message = "O status deve ter no máximo 50 caracteres")
        String status,

        @Schema(description = "Tipo de consulta (ex: Avaliação, Retorno, Rotina, Urgência)", example = "Rotina")
        @NotBlank(message = "O tipo de consulta é obrigatório")
        @Size(max = 100, message = "O tipo de consulta deve ter no máximo 100 caracteres")
        String tipoConsulta
) {
}
