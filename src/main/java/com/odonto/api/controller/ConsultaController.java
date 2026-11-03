package com.odonto.api.controller;

import com.odonto.api.dto.ConsultaRequestDTO;
import com.odonto.api.dto.ConsultaResponseDTO;
import com.odonto.api.dto.ConsultaStatusDTO;
import com.odonto.api.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Endpoints para agendamento, atualização e gerenciamento de consultas odontológicas")
public class ConsultaController {

    private final ConsultaService consultaService;

    @GetMapping
    @Operation(
            summary = "Listar consultas",
            description = "Retorna todas as consultas cadastradas, permitindo filtrar opcionalmente por paciente, data, status ou tipo de consulta"
    )
    @ApiResponse(responseCode = "200", description = "Lista de consultas retornada com sucesso")
    public ResponseEntity<List<ConsultaResponseDTO>> listarTodas(
            @Parameter(description = "Filtro opcional pelo ID do paciente", example = "1")
            @RequestParam(required = false) Long pacienteId,
            @Parameter(description = "Filtro opcional pela data da consulta (AAAA-MM-DD)", example = "2026-11-15")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @Parameter(description = "Filtro opcional pelo status da consulta", example = "AGENDADA")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filtro opcional pelo tipo de consulta", example = "Rotina")
            @RequestParam(required = false) String tipoConsulta
    ) {
        return ResponseEntity.ok(consultaService.listarTodas(pacienteId, data, status, tipoConsulta));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por ID", description = "Retorna os detalhes de uma consulta específica pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(
            @Parameter(description = "ID da consulta", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    @Operation(
            summary = "Listar histórico de consultas de um paciente",
            description = "Retorna todas as consultas associadas a um paciente específico, ordenadas da mais recente para a mais antiga"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico de consultas retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorPaciente(
            @Parameter(description = "ID do paciente", example = "1")
            @PathVariable Long pacienteId
    ) {
        return ResponseEntity.ok(consultaService.listarPorPaciente(pacienteId));
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar consulta",
            description = "Agenda ou registra uma nova consulta e a adiciona automaticamente ao histórico de consultas do paciente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Paciente informado não encontrado")
    })
    public ResponseEntity<ConsultaResponseDTO> criar(@Valid @RequestBody ConsultaRequestDTO dto) {
        ConsultaResponseDTO criada = consultaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar consulta", description = "Atualiza todos os dados de uma consulta existente pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Consulta ou paciente não encontrado")
    })
    public ResponseEntity<ConsultaResponseDTO> atualizar(
            @Parameter(description = "ID da consulta", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ConsultaRequestDTO dto
    ) {
        return ResponseEntity.ok(consultaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da consulta", description = "Atualiza apenas o status de uma consulta específica (ex: AGENDADA -> REALIZADA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status da consulta atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido ou em branco"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public ResponseEntity<ConsultaResponseDTO> atualizarStatus(
            @Parameter(description = "ID da consulta", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ConsultaStatusDTO dto
    ) {
        return ResponseEntity.ok(consultaService.atualizarStatus(id, dto.status()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir consulta", description = "Remove uma consulta pelo seu ID e a desvincula do histórico do paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consulta excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da consulta", example = "1")
            @PathVariable Long id
    ) {
        consultaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
