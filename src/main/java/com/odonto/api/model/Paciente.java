package com.odonto.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Builder.Default
    @OrderBy("data DESC, hora DESC")
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consulta> consultas = new ArrayList<>();

    public void adicionarConsulta(Consulta consulta) {
        this.consultas.add(consulta);
        consulta.setPaciente(this);
    }

    public void removerConsulta(Consulta consulta) {
        this.consultas.remove(consulta);
        consulta.setPaciente(null);
    }

    /**
     * Calcula automaticamente a idade do paciente com base na data de nascimento.
     */
    @Transient
    public Integer getIdade() {
        if (this.dataNascimento == null) {
            return null;
        }
        return Period.between(this.dataNascimento, LocalDate.now()).getYears();
    }
}
