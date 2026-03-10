# Finance Control API

API REST para controle de gastos e ganhos, permitindo o cadastro de categorias, subcategorias e lançamentos financeiros, além de consulta de balanço por período.

## Tecnologias

- Java 17
- Spring Boot 3.4.3
- Maven
- MySQL (produção) / H2 (desenvolvimento)
- Lombok
- SpringDoc OpenAPI (Swagger UI)
- Spring Actuator

## Arquitetura

O projeto segue os princípios de **Arquitetura Hexagonal**, **Clean Code** e **SOLID**.

```
com.financecontrol.api
├── adapters
│   ├── controller         → controllers REST, documentação Swagger e handler de exceções
│   │   ├── request        → DTOs de entrada (records)
│   │   └── response       → DTOs de saída (records)
│   └── out
│       └── persistence    → entidades JPA, mappers e adapters de repositório
├── domain                 → regras de negócio puras (sem dependências de framework)
│   ├── categoria
│   ├── subcategoria
│   └── shared             → exceções, mensagens de erro e paginação
└── infrastructure
    ├── config             → configurações técnicas (OpenAPI, Security)
    └── filter             → filtro de autenticação via api-key
```

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker e Docker Compose (recomendado)
- MySQL 8+ (apenas para produção sem Docker)

## Como rodar

### Com Docker (recomendado)

Sobe a API junto com o MySQL com um único comando:

```bash
docker-compose up --build
```

A API estará disponível em `http://localhost:8080`.  
O MySQL ficará disponível na porta `3306`.

Para parar:

```bash
docker-compose down
```

Para parar e remover os dados do banco:

```bash
docker-compose down -v
```

---

### Desenvolvimento local (H2 em memória)

```bash
mvn spring-boot:run
```

### Produção local (MySQL)

```bash
SPRING_PROFILES_ACTIVE=prod \
  DB_URL=jdbc:mysql://localhost:3306/finance_control \
  DB_USERNAME=finance \
  DB_PASSWORD=itau-finance-control \
  DB_DRIVER=com.mysql.cj.jdbc.Driver \
  mvn spring-boot:run
```

### Variáveis de ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `API_KEY` | Chave de autenticação da API | `aXRhw7o=` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo (`dev` ou `prod`) | `dev` |
| `DB_URL` | URL de conexão com o banco | H2 em memória |
| `DB_USERNAME` | Usuário do banco | `sa` |
| `DB_PASSWORD` | Senha do banco | _(vazio)_ |
| `DB_DRIVER` | Driver JDBC | `org.h2.Driver` |

## Autenticação

Todas as requisições para `/v1/**` exigem o seguinte **header HTTP**:

| Header | Valor |
|---|---|
| `api-key` | `aXRhw7o=` |

Requisições sem esse header retornam `401 Unauthorized`.

## Documentação Swagger

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

O Swagger já vem configurado com o campo `api-key` no header. Basta clicar em **Authorize**, informar o valor `aXRhw7o=` e todas as requisições feitas pela interface já incluirão o header automaticamente.

## Endpoints

### Health

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/health` | Verifica se a aplicação está no ar |

---

### Categorias

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/categorias` | Lista categorias. Filtros opcionais: `nome`, `page`, `size` |
| `GET` | `/v1/categorias/{id}` | Busca uma categoria pelo ID |
| `POST` | `/v1/categorias` | Cria uma nova categoria |
| `PUT` | `/v1/categorias/{id}` | Atualiza o nome de uma categoria |
| `DELETE` | `/v1/categorias/{id}` | Remove uma categoria (não permitido se houver lançamentos nas subcategorias) |

---

### Subcategorias

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/subcategorias` | Lista subcategorias. Filtros opcionais: `nome`, `id_subcategoria`, `page`, `size` |
| `GET` | `/v1/subcategorias/{id}` | Busca uma subcategoria pelo ID |
| `POST` | `/v1/subcategorias` | Cria uma nova subcategoria |
| `PUT` | `/v1/subcategorias/{id}` | Atualiza uma subcategoria |
| `DELETE` | `/v1/subcategorias/{id}` | Remove uma subcategoria (não permitido se houver lançamentos atrelados) |

---

### Lançamentos

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/lancamentos` | Lista lançamentos. Filtros opcionais: `id_subcategoria`, `data_inicio` (dd/MM/yyyy), `data_fim` (dd/MM/yyyy), `page`, `size` |
| `GET` | `/v1/lancamentos/{id}` | Busca um lançamento pelo ID |
| `POST` | `/v1/lancamentos` | Cria um novo lançamento |
| `PUT` | `/v1/lancamentos/{id}` | Atualiza um lançamento |
| `DELETE` | `/v1/lancamentos/{id}` | Remove um lançamento |

---

### Balanço

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/balanco` | Consulta o balanço geral de um período. Obrigatórios: `data_inicio` (dd/MM/yyyy), `data_fim` (dd/MM/yyyy). Opcional: `id_categoria` |

> O campo `categoria` só é retornado quando `id_categoria` é informado. O balanço é calculado em tempo real com base nos lançamentos existentes, sem gravar nada no banco.

---

> Para detalhes de campos, exemplos de request/response e códigos de erro, consulte o [**Swagger**](http://localhost:8080/swagger-ui.html).

---

### Códigos de status utilizados

| Status | Significado |
|---|---|
| `200` | Sucesso |
| `201` | Recurso criado |
| `204` | Recurso removido |
| `400` | Dados inválidos |
| `401` | api-key ausente ou inválida |
| `404` | Recurso não encontrado |
| `409` | Conflito (nome duplicado) |
| `422` | Operação não permitida (ex: subcategoria com lançamentos) |

---

## Observabilidade

A aplicação utiliza o **Spring Actuator** para monitoramento. Os seguintes endpoints estão disponíveis **sem necessidade de api-key**:

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Retorna o status da aplicação (`UP` ou `DOWN`) |
| `GET /actuator/metrics` | Lista as métricas disponíveis |

Para consultar uma métrica específica:
```
GET /actuator/metrics/http.server.requests.active
GET /actuator/metrics/application.ready.time
```

## Testes

```bash
mvn test
```



