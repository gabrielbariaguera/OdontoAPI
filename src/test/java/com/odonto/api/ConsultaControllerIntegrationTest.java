package com.odonto.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odonto.api.dto.ConsultaRequestDTO;
import com.odonto.api.dto.ConsultaStatusDTO;
import com.odonto.api.dto.PacienteRequestDTO;
import com.odonto.api.repository.ConsultaRepository;
import com.odonto.api.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @BeforeEach
    void setUp() {
        consultaRepository.deleteAll();
        pacienteRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve realizar o CRUD completo de consultas e armazenar todas as consultas no histórico do paciente")
    void deveRealizarCrudDeConsultasEManterHistoricoNoPaciente() throws Exception {
        // 1. Criar um paciente
        PacienteRequestDTO novoPaciente = new PacienteRequestDTO(
                "Ana Beatriz Souza",
                "987.654.321-11",
                "(11) 98888-7777",
                "Rua Augusta, 500 - Consolação, São Paulo - SP",
                LocalDate.of(1992, 8, 15),
                "Paciente sem alergias."
        );

        String pacienteJson = mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoPaciente)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long pacienteId = objectMapper.readTree(pacienteJson).get("id").asLong();

        // 2. Criar primeira consulta (já realizada no passado)
        ConsultaRequestDTO consultaPassada = new ConsultaRequestDTO(
                pacienteId,
                LocalDate.of(2026, 5, 10),
                LocalTime.of(9, 30),
                "Avaliação Geral e Raio-X",
                "REALIZADA",
                "Avaliação"
        );

        String consulta1Json = mockMvc.perform(post("/api/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consultaPassada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pacienteId", is(pacienteId.intValue())))
                .andExpect(jsonPath("$.pacienteNome", is("Ana Beatriz Souza")))
                .andExpect(jsonPath("$.data", is("2026-05-10")))
                .andExpect(jsonPath("$.hora", is("09:30")))
                .andExpect(jsonPath("$.procedimento", is("Avaliação Geral e Raio-X")))
                .andExpect(jsonPath("$.status", is("REALIZADA")))
                .andExpect(jsonPath("$.tipoConsulta", is("Avaliação")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long consulta1Id = objectMapper.readTree(consulta1Json).get("id").asLong();

        // 3. Criar segunda consulta (agendada)
        ConsultaRequestDTO consultaFutura = new ConsultaRequestDTO(
                pacienteId,
                LocalDate.of(2026, 11, 20),
                LocalTime.of(14, 0),
                "Limpeza e Profilaxia",
                "AGENDADA",
                "Rotina"
        );

        String consulta2Json = mockMvc.perform(post("/api/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consultaFutura)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long consulta2Id = objectMapper.readTree(consulta2Json).get("id").asLong();

        // 4. Verificar que o Paciente armazena todas as consultas que ele já fez / tem cadastradas
        mockMvc.perform(get("/api/pacientes/{id}", pacienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(pacienteId.intValue())))
                .andExpect(jsonPath("$.consultas", hasSize(2)))
                .andExpect(jsonPath("$.consultas[*].procedimento", containsInAnyOrder(
                        "Avaliação Geral e Raio-X",
                        "Limpeza e Profilaxia"
                )))
                .andExpect(jsonPath("$.consultas[*].status", containsInAnyOrder("REALIZADA", "AGENDADA")))
                .andExpect(jsonPath("$.consultas[*].tipoConsulta", containsInAnyOrder("Avaliação", "Rotina")));

        // 5. Atualizar a segunda consulta (PUT)
        ConsultaRequestDTO consulta2Atualizada = new ConsultaRequestDTO(
                pacienteId,
                LocalDate.of(2026, 11, 21),
                LocalTime.of(15, 30),
                "Restauração em Resina",
                "CONFIRMADA",
                "Retorno"
        );

        mockMvc.perform(put("/api/consultas/{id}", consulta2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consulta2Atualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is("2026-11-21")))
                .andExpect(jsonPath("$.hora", is("15:30")))
                .andExpect(jsonPath("$.procedimento", is("Restauração em Resina")))
                .andExpect(jsonPath("$.status", is("CONFIRMADA")))
                .andExpect(jsonPath("$.tipoConsulta", is("Retorno")));

        // 6. Atualizar status da segunda consulta para REALIZADA (PATCH)
        mockMvc.perform(patch("/api/consultas/{id}/status", consulta2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ConsultaStatusDTO("REALIZADA"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REALIZADA")));

        // 7. Listar consultas por paciente
        mockMvc.perform(get("/api/consultas/paciente/{pacienteId}", pacienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // 8. Excluir a primeira consulta e verificar que o paciente mantém a restante
        mockMvc.perform(delete("/api/consultas/{id}", consulta1Id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/pacientes/{id}", pacienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultas", hasSize(1)))
                .andExpect(jsonPath("$.consultas[0].id", is(consulta2Id.intValue())))
                .andExpect(jsonPath("$.consultas[0].procedimento", is("Restauração em Resina")))
                .andExpect(jsonPath("$.consultas[0].status", is("REALIZADA")));
    }
}
