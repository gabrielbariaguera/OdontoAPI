package com.odonto.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.odonto.api.model.Consulta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Resumo de uma consulta vinculado ao histórico do paciente")
public record ConsultaResumoDTO(
        @Schema(description = "ID único da consulta", example = "10")
        Long id,

        @Schema(description = "Data da consulta", example = "2026-11-15")
        LocalDate data,

        @Schema(description = "Hora da consulta (HH:mm)", example = "14:30", type = "string")
        @JsonFormat(pattern = "HH:mm")
        LocalTime hora,

        @Schema(description = "Procedimento realizado ou agendado", example = "Limpeza e Profilaxia")
        String procedimento,

        @Schema(description = "Status da consulta", example = "REALIZADA")
        String status,

        @Schema(description = "Tipo da consulta", example = "Rotina")
        String tipoConsulta
) {
    public static ConsultaResumoDTO fromEntity(Consulta consulta) {
        return new ConsultaResumoDTO(
                consulta.getId(),
                consulta.getData(),
                consulta.getHora(),
                consulta.getProcedimento(),
                consulta.getStatus(),
                consulta.getTipoConsulta()
        );
    }
}
