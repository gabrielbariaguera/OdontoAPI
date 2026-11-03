package com.odonto.api.controller;

import com.odonto.api.dto.PacienteRequestDTO;
import com.odonto.api.dto.PacienteResponseDTO;
import com.odonto.api.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Endpoints para gerenciamento completo de pacientes e visualização do histórico de consultas")
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    @Operation(summary = "Listar pacientes", description = "Retorna todos os pacientes cadastrados ou filtra pelo nome caso informado")
    @ApiResponse(responseCode = "200", description = "Lista de pacientes retornada com sucesso")
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos(
            @Parameter(description = "Filtro opcional por nome do paciente", example = "Maria")
            @RequestParam(required = false) String nome
    ) {
        return ResponseEntity.ok(pacienteService.listarTodos(nome));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID", description = "Retorna os detalhes de um paciente específico e todas as suas consultas pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<PacienteResponseDTO> buscarPorId(
            @Parameter(description = "ID do paciente", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar paciente por CPF", description = "Retorna os detalhes de um paciente específico pelo seu CPF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado com o CPF informado")
    })
    public ResponseEntity<PacienteResponseDTO> buscarPorCpf(
            @Parameter(description = "CPF do paciente", example = "123.456.789-00")
            @PathVariable String cpf
    ) {
        return ResponseEntity.ok(pacienteService.buscarPorCpf(cpf));
    }

    @PostMapping
    @Operation(summary = "Cadastrar paciente", description = "Cria um novo paciente e calcula sua idade automaticamente com base na data de nascimento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paciente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Já existe um paciente cadastrado com o CPF informado")
    })
    public ResponseEntity<PacienteResponseDTO> criar(@Valid @RequestBody PacienteRequestDTO dto) {
        PacienteResponseDTO criado = pacienteService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar paciente", description = "Atualiza os dados cadastrais de um paciente existente pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "409", description = "CPF já pertence a outro paciente cadastrado")
    })
    public ResponseEntity<PacienteResponseDTO> atualizar(
            @Parameter(description = "ID do paciente", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO dto
    ) {
        return ResponseEntity.ok(pacienteService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir paciente", description = "Remove um paciente e todas as suas consultas associadas pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paciente excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do paciente", example = "1")
            @PathVariable Long id
    ) {
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
