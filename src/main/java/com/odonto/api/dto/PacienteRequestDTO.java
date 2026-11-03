package com.odonto.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacienteRequestDTO(

        @Schema(description = "Nome completo do paciente", example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @Schema(description = "CPF do paciente (apenas números ou formatado)", example = "123.456.789-00")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(
                regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$",
                message = "O CPF deve conter 11 dígitos ou estar no formato 000.000.000-00"
        )
        String cpf,

        @Schema(description = "Telefone de contato do paciente", example = "(11) 98765-4321")
        @NotBlank(message = "O telefone é obrigatório")
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String telefone,

        @Schema(description = "Endereço completo do paciente", example = "Rua das Flores, 123 - Centro, São Paulo - SP")
        @NotBlank(message = "O endereço é obrigatório")
        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
        String endereco,

        @Schema(description = "Data de nascimento do paciente", example = "1995-05-20")
        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve ser uma data no passado")
        LocalDate dataNascimento,

        @Schema(description = "Observações clínicas ou gerais sobre o paciente", example = "Paciente alérgico a dipirona.")
        String observacoes
) {
}
