# 🦷 OdontoAPI — Sistema de Gestão Odontológica

API RESTful desenvolvida em **Java 21** com **Spring Boot 3** e **PostgreSQL** para o gerenciamento completo de **Pacientes** e **Consultas Odontológicas**.

---

## ✨ Funcionalidades Principais

### 👤 Gestão de Pacientes (`/api/pacientes`)
- **CRUD Completo**: Cadastro, listagem (com filtro por nome), busca por ID, busca por CPF, atualização e exclusão.
- **Campos do Paciente**:
  - `nome` (obrigatório)
  - `cpf` (obrigatório, único, aceita apenas números ou formato `000.000.000-00`)
  - `telefone` (obrigatório)
  - `endereco` (obrigatório)
  - `dataNascimento` (obrigatória, usada para calcular a **idade automaticamente**)
  - `idade` (calculada dinamicamente pela API com base na data de nascimento)
  - `observacoes` (texto livre opcional)
  - `consultas` (lista com todo o **histórico de consultas** que o paciente já realizou ou possui agendadas)

### 📅 Gestão de Consultas (`/api/consultas`)
- **CRUD Completo**: Agendamento/registro, listagem (com filtros combináveis por paciente, data, status e tipo), busca por ID, histórico por paciente, atualização completa, atualização rápida de status e exclusão.
- **Campos da Consulta**:
  - `paciente` (`pacienteId`) — vínculo com o paciente
  - `data` (`AAAA-MM-DD`)
  - `hora` (`HH:mm`)
  - `procedimento` (ex: `"Limpeza e Profilaxia"`, `"Canal"`, `"Restauração"`)
  - `status` (ex: `"AGENDADA"`, `"CONFIRMADA"`, `"REALIZADA"`, `"CANCELADA"`)
  - `tipoConsulta` (ex: `"Avaliação"`, `"Rotina"`, `"Retorno"`, `"Urgência"`)
- **Sincronização Automática**: Toda consulta criada para um paciente é automaticamente armazenada e exibida na lista `consultas` dentro do cadastro do paciente.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.5**
  - **Spring Web** (API REST)
  - **Spring Data JPA / Hibernate** (Persistência de dados)
  - **Bean Validation** (Validação de dados de entrada)
- **PostgreSQL** (Banco de dados relacional de produção/desenvolvimento)
- **H2 Database** (Banco em memória para execução rápida de testes integrados)
- **SpringDoc OpenAPI 2.6.0 (Swagger UI)** (Documentação interativa da API)
- **Lombok** (Redução de código boilerplate)
- **JUnit 5 & MockMvc** (Testes automatizados de integração)

---

## 📂 Estrutura do Projeto

```text
OdontoAPI/
├── src/
│   ├── main/
│   │   ├── java/com/odonto/api/
│   │   │   ├── config/          # Configuração do Swagger / OpenAPI (OpenApiConfig)
│   │   │   ├── controller/      # Endpoints REST (PacienteController, ConsultaController)
│   │   │   ├── dto/             # Objetos de transferência de dados (Request / Response DTOs)
│   │   │   ├── exception/       # Tratamento global de erros (ProblemDetail RFC 7807)
│   │   │   ├── model/           # Entidades JPA (Paciente, Consulta)
│   │   │   ├── repository/      # Repositórios Spring Data JPA
│   │   │   ├── service/         # Regras de negócio e transações
│   │   │   └── OdontoApiApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/odonto/api/ # Testes de integração (MockMvc)
│       └── resources/
│           └── application.properties
└── pom.xml
```

---

## 🚀 Passo a Passo para Executar o Projeto

### 1. Pré-requisitos
Certifique-se de ter instalado em sua máquina:
- **Java JDK 21** ou superior
- **Maven 3.9+** (ou utilize o Maven embutido na sua IDE, como IntelliJ IDEA / Eclipse / VS Code)
- **PostgreSQL** instalado e em execução

---

### 2. Configurar o Banco de Dados PostgreSQL
Abra o terminal do PostgreSQL (`psql`) ou o **pgAdmin** e crie o banco de dados `odonto_db`:

```sql
CREATE DATABASE odonto_db;
```

As configurações padrão estão no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/odonto_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
```

> 💡 **Dica**: Você pode alterar o usuário e senha diretamente no `application.properties` ou definir as variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. O Hibernate criará e atualizará as tabelas `pacientes` e `consultas` automaticamente (`spring.jpa.hibernate.ddl-auto=update`).

---

### 3. Compilar e Rodar a Aplicação
No terminal, na pasta raiz do projeto (`OdontoAPI`), execute:

```bash
mvn spring-boot:run
```

A API iniciará na porta **`8080`**:
- **URL Base**: `http://localhost:8080`

---

### 4. Executar os Testes Automatizados
Os testes de integração utilizam o banco em memória **H2**, portanto **não precisam do PostgreSQL rodando** para funcionar:

```bash
mvn test
```

---

## 📖 Documentação Interativa (Swagger UI)

Com a aplicação rodando, você pode visualizar e testar todas as rotas diretamente pelo navegador através do **Swagger UI**:

- **Swagger UI (Interface Interativa)**:  
  👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Especificação OpenAPI (JSON)**:  
  👉 [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## 🛣️ Guia Completo de Rotas da API

### 1️⃣ Rotas de Pacientes (`/api/pacientes`)

| Método | Rota | Descrição | Status de Sucesso |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/pacientes` | Lista todos os pacientes (aceita filtro opcional `?nome=...`) | `200 OK` |
| `GET` | `/api/pacientes/{id}` | Busca um paciente pelo seu ID | `200 OK` |
| `GET` | `/api/pacientes/cpf/{cpf}` | Busca um paciente pelo seu CPF | `200 OK` |
| `POST` | `/api/pacientes` | Cadastra um novo paciente | `201 Created` |
| `PUT` | `/api/pacientes/{id}` | Atualiza os dados de um paciente existente | `200 OK` |
| `DELETE` | `/api/pacientes/{id}` | Exclui um paciente e suas consultas associadas | `204 No Content` |

#### 📌 Exemplo: Cadastrar Paciente (`POST /api/pacientes`)

**Request Body (`application/json`):**
```json
{
  "nome": "Maria Silva",
  "cpf": "123.456.789-00",
  "telefone": "(11) 98765-4321",
  "endereco": "Rua das Flores, 123 - Centro, São Paulo - SP",
  "dataNascimento": "1995-05-20",
  "observacoes": "Paciente alérgica a dipirona."
}
```

**Response (`201 Created`):**
```json
{
  "id": 1,
  "nome": "Maria Silva",
  "cpf": "123.456.789-00",
  "telefone": "(11) 98765-4321",
  "endereco": "Rua das Flores, 123 - Centro, São Paulo - SP",
  "dataNascimento": "1995-05-20",
  "idade": 31,
  "observacoes": "Paciente alérgica a dipirona.",
  "consultas": []
}
```

#### 📌 Exemplo: Buscar Paciente com Histórico de Consultas (`GET /api/pacientes/1`)

**Response (`200 OK`):**
```json
{
  "id": 1,
  "nome": "Maria Silva",
  "cpf": "123.456.789-00",
  "telefone": "(11) 98765-4321",
  "endereco": "Rua das Flores, 123 - Centro, São Paulo - SP",
  "dataNascimento": "1995-05-20",
  "idade": 31,
  "observacoes": "Paciente alérgica a dipirona.",
  "consultas": [
    {
      "id": 10,
      "data": "2026-11-15",
      "hora": "14:30",
      "procedimento": "Limpeza e Profilaxia",
      "status": "REALIZADA",
      "tipoConsulta": "Rotina"
    }
  ]
}
```

---

### 2️⃣ Rotas de Consultas (`/api/consultas`)

| Método | Rota | Descrição | Status de Sucesso |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/consultas` | Lista todas as consultas (filtros opcionais: `pacienteId`, `data`, `status`, `tipoConsulta`) | `200 OK` |
| `GET` | `/api/consultas/{id}` | Busca os detalhes de uma consulta pelo ID | `200 OK` |
| `GET` | `/api/consultas/paciente/{pacienteId}` | Lista todas as consultas de um paciente específico | `200 OK` |
| `POST` | `/api/consultas` | Cria/agenda uma consulta e a adiciona ao histórico do paciente | `201 Created` |
| `PUT` | `/api/consultas/{id}` | Atualiza todos os dados de uma consulta | `200 OK` |
| `PATCH` | `/api/consultas/{id}/status` | Atualiza apenas o status da consulta | `200 OK` |
| `DELETE` | `/api/consultas/{id}` | Exclui uma consulta pelo ID | `204 No Content` |

#### 📌 Exemplo: Criar Consulta (`POST /api/consultas`)

**Request Body (`application/json`):**
```json
{
  "pacienteId": 1,
  "data": "2026-11-15",
  "hora": "14:30",
  "procedimento": "Limpeza e Profilaxia",
  "status": "AGENDADA",
  "tipoConsulta": "Rotina"
}
```

**Response (`201 Created`):**
```json
{
  "id": 10,
  "pacienteId": 1,
  "pacienteNome": "Maria Silva",
  "pacienteCpf": "123.456.789-00",
  "data": "2026-11-15",
  "hora": "14:30",
  "procedimento": "Limpeza e Profilaxia",
  "status": "AGENDADA",
  "tipoConsulta": "Rotina"
}
```

#### 📌 Exemplo: Atualizar Apenas o Status da Consulta (`PATCH /api/consultas/10/status`)

**Request Body (`application/json`):**
```json
{
  "status": "REALIZADA"
}
```

**Response (`200 OK`):**
```json
{
  "id": 10,
  "pacienteId": 1,
  "pacienteNome": "Maria Silva",
  "pacienteCpf": "123.456.789-00",
  "data": "2026-11-15",
  "hora": "14:30",
  "procedimento": "Limpeza e Profilaxia",
  "status": "REALIZADA",
  "tipoConsulta": "Rotina"
}
```

#### 📌 Exemplos de Filtros na Listagem de Consultas (`GET /api/consultas`)
- Filtrar por data: `GET /api/consultas?data=2026-11-15`
- Filtrar por status: `GET /api/consultas?status=AGENDADA`
- Filtrar por tipo de consulta: `GET /api/consultas?tipoConsulta=Rotina`
- Filtrar por paciente e status: `GET /api/consultas?pacienteId=1&status=REALIZADA`

---

## ⚠️ Padrão de Respostas de Erro

A API utiliza o padrão **Problem Details for HTTP APIs (RFC 7807)** através do `GlobalExceptionHandler`:

- **`400 Bad Request`**: Erro de validação de campos (`@NotBlank`, `@NotNull`, `@Past`, formato de CPF, etc.).
- **`404 Not Found`**: Paciente ou consulta não encontrados.
- **`409 Conflict`**: Violação de regra de negócio (ex: CPF já cadastrado para outro paciente).
