# Finance Control API

API REST para controle de gastos e ganhos, permitindo o cadastro de categorias, subcategorias e lançamentos financeiros, além de consulta de balanço por período.

## Tecnologias

- Java 17
- Spring Boot 3.4.3
- Maven
- MySQL (produção) / H2 (desenvolvimento)
- Lombok
- Spring Actuator

## Arquitetura

O projeto segue os princípios de **Arquitetura Hexagonal**, **Clean Code** e **SOLID**.

```
com.financecontrol.api
├── adapters
│   └── controller         → controllers REST e handlers de exceção
│       ├── request        → DTOs de entrada
│       └── response       → DTOs de saída
├── domain                 → regras de negócio (entidades e serviços)
└── infrastructure
    ├── config             → configurações técnicas
    └── filter             → filtros HTTP (autenticação via api-key)
```

## Pré-requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+ (apenas para produção)

## Como rodar

### Desenvolvimento (H2 em memória)

```bash
mvn spring-boot:run
```

### Produção (MySQL)

```bash
SPRING_PROFILES_ACTIVE=prod DB_URL=jdbc:mysql://localhost:3306/finance_control DB_USERNAME=root DB_PASSWORD=root mvn spring-boot:run
```

### Variáveis de ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `API_KEY` | Chave de autenticação da API | `aXRhw7o=` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo (`dev` ou `prod`) | `dev` |
| `DB_URL` | URL de conexão com o banco | H2 em memória |
| `DB_USERNAME` | Usuário do banco | `root` |
| `DB_PASSWORD` | Senha do banco | `root` |

## Autenticação

Todas as requisições para `/v1/**` exigem o header:

```
api-key: aXRhw7o=
```

Requisições sem esse header retornam `401 Unauthorized`.

## Endpoints

### Health
```
GET /v1/health
Header: api-key: aXRhw7o=
```

## Observabilidade

A aplicação utiliza o **Spring Actuator** para monitoramento. Os seguintes endpoints estão disponíveis **sem necessidade de api-key**:

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Retorna o status da aplicação (`UP` ou `DOWN`) |
| `GET /actuator/metrics` | Retorna métricas de requisições HTTP e tempo de inicialização |

As métricas disponíveis são:
- `application.ready.time` — tempo até a aplicação ficar pronta
- `application.started.time` — tempo de inicialização
- `http.server.requests.active` — requisições HTTP ativas

Para consultar uma métrica específica:
```
GET /actuator/metrics/http.server.requests.active
GET /actuator/metrics/application.ready.time
```

## Testes

```bash
mvn test
```
