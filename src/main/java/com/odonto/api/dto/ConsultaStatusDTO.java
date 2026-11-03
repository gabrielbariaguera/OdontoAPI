package com.odonto.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultaStatusDTO(
        @Schema(description = "Novo status da consulta", example = "CONCLUIDA")
        @NotBlank(message = "O status da consulta é obrigatório")
        @Size(max = 50, message = "O status deve ter no máximo 50 caracteres")
        String status
) {
}
