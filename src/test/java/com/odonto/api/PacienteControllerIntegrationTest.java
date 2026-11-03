package com.odonto.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odonto.api.dto.PacienteRequestDTO;
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
import java.time.Period;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PacienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PacienteRepository pacienteRepository;

    @BeforeEach
    void setUp() {
        pacienteRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve realizar o ciclo completo de CRUD de um paciente e calcular a idade automaticamente")
    void deveRealizarCrudDePacienteComSucesso() throws Exception {
        LocalDate dataNascimento = LocalDate.now().minusYears(30).minusDays(10);
        int idadeEsperada = Period.between(dataNascimento, LocalDate.now()).getYears();

        PacienteRequestDTO novoPaciente = new PacienteRequestDTO(
                "João da Silva",
                "123.456.789-00",
                "(11) 99999-8888",
                "Rua das Palmeiras, 450 - Jardim Paulista, São Paulo - SP",
                dataNascimento,
                "Paciente com sensibilidade dentária."
        );

        // 1. CREATE
        String responseJson = mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoPaciente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("João da Silva")))
                .andExpect(jsonPath("$.cpf", is("123.456.789-00")))
                .andExpect(jsonPath("$.endereco", is("Rua das Palmeiras, 450 - Jardim Paulista, São Paulo - SP")))
                .andExpect(jsonPath("$.idade", is(idadeEsperada)))
                .andExpect(jsonPath("$.observacoes", is("Paciente com sensibilidade dentária.")))
                .andExpect(jsonPath("$.consultas", empty()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long idCriado = objectMapper.readTree(responseJson).get("id").asLong();

        // 2. READ BY ID
        mockMvc.perform(get("/api/pacientes/{id}", idCriado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(idCriado.intValue())))
                .andExpect(jsonPath("$.endereco", is("Rua das Palmeiras, 450 - Jardim Paulista, São Paulo - SP")))
                .andExpect(jsonPath("$.idade", is(idadeEsperada)));

        // 3. UPDATE (mudando endereço, data de nascimento e observações)
        LocalDate novaDataNascimento = LocalDate.now().minusYears(25);
        PacienteRequestDTO pacienteAtualizado = new PacienteRequestDTO(
                "João da Silva Santos",
                "123.456.789-00",
                "(11) 97777-6666",
                "Av. Paulista, 1000 - Bela Vista, São Paulo - SP",
                novaDataNascimento,
                "Sem restrições."
        );

        mockMvc.perform(put("/api/pacientes/{id}", idCriado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("João da Silva Santos")))
                .andExpect(jsonPath("$.endereco", is("Av. Paulista, 1000 - Bela Vista, São Paulo - SP")))
                .andExpect(jsonPath("$.idade", is(25)))
                .andExpect(jsonPath("$.observacoes", is("Sem restrições.")));

        // 4. DELETE
        mockMvc.perform(delete("/api/pacientes/{id}", idCriado))
                .andExpect(status().isNoContent());

        // 5. VERIFY NOT FOUND
        mockMvc.perform(get("/api/pacientes/{id}", idCriado))
                .andExpect(status().isNotFound());
    }
}
