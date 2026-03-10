# Finance Control API

API REST para controle de gastos e ganhos pessoais, desenvolvida como desafio técnico. Permite o cadastro de **categorias**, **subcategorias** e **lançamentos financeiros**, além de consulta de **balanço por período** com cálculo em tempo real.

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.4.3 | Framework base |
| Spring Data JPA | 3.4.3 | Persistência e repositórios |
| Spring Actuator | 3.4.3 | Observabilidade e healthcheck |
| SpringDoc OpenAPI | 2.8.5 | Documentação Swagger UI |
| Lombok | — | Redução de boilerplate |
| MySQL | 8 | Banco de dados de produção |
| H2 | — | Banco em memória para desenvolvimento |
| Maven | 3.8+ | Build e gerenciamento de dependências |
| Docker / Docker Compose | — | Containerização e orquestração |

---

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
│   ├── lancamento
│   ├── balanco
│   └── shared             → exceções, mensagens de erro e paginação
└── infrastructure
    ├── config             → configurações técnicas (OpenAPI, Security)
    └── filter             → filtro de autenticação via api-key
```

### Decisões técnicas

- **Domínio isolado**: as classes de domínio (`Categoria`, `Subcategoria`, `Lancamento`) são POJOs puros, sem nenhuma anotação de framework. As anotações JPA ficam nas entidades de persistência (`*Entity`), dentro de `adapters.out.persistence`.
- **Porta de repositório**: cada domínio define uma interface (`*RepositoryPort`) que o serviço usa. A implementação JPA fica no adaptador, garantindo que o domínio não dependa do Spring Data.
- **Paginação própria**: criado um record `PaginaResultado<T>` para expor somente os campos essenciais da paginação, sem vazar os metadados internos do Spring Data para o cliente.
- **Validação em dois níveis**: Bean Validation no DTO de entrada (`@NotBlank`, `@NotNull`) + validação de negócio no domínio (`validar()`), garantindo integridade independente de quem aciona o serviço.
- **Autenticação via api-key**: implementada com `OncePerRequestFilter` do Spring Security, retornando `401` em JSON estruturado quando a chave está ausente ou inválida.
- **Sem cache**: conforme restrição do desafio, nenhum mecanismo de caching (Redis, Memcached etc.) foi utilizado.

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker e Docker Compose (recomendado)
- MySQL 8+ (apenas para rodar sem Docker)

---

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

Nesse modo, o banco H2 é criado em memória automaticamente. Nenhuma configuração adicional é necessária.

---

### Produção local (MySQL)

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_URL="jdbc:mysql://localhost:3306/finance_control"
$env:DB_USERNAME="finance"
$env:DB_PASSWORD="itau-finance-control"
$env:DB_DRIVER="com.mysql.cj.jdbc.Driver"
mvn spring-boot:run
```

---

### Variáveis de ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `API_KEY` | Chave de autenticação da API | `aXRhw7o=` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo (`dev` ou `prod`) | `dev` |
| `DB_URL` | URL de conexão com o banco | H2 em memória |
| `DB_USERNAME` | Usuário do banco | `sa` |
| `DB_PASSWORD` | Senha do banco | _(vazio)_ |
| `DB_DRIVER` | Driver JDBC | `org.h2.Driver` |

---

## Autenticação

Todas as requisições para `/v1/**` exigem o seguinte **header HTTP**:

| Header | Valor |
|---|---|
| `api-key` | `aXRhw7o=` |

Requisições sem esse header retornam `401 Unauthorized`.

---

## Documentação Swagger

| Ambiente | URL |
|---|---|
| Local | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| Produção | [https://itau-finance-control-api-production.up.railway.app/swagger-ui.html](https://itau-finance-control-api-production.up.railway.app/swagger-ui.html) |

O Swagger já vem configurado com o campo `api-key` no header. Basta clicar em **Authorize**, informar o valor `aXRhw7o=` e todas as requisições feitas pela interface já incluirão o header automaticamente.

---

## Endpoints

### Health

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/health` | Verifica se a aplicação está no ar |

---

### Categorias

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/categorias` | Lista categorias (filtros opcionais: `nome`, `page`, `size`) |
| `GET` | `/v1/categorias/{id}` | Busca uma categoria pelo ID |
| `POST` | `/v1/categorias` | Cria uma nova categoria |
| `PUT` | `/v1/categorias/{id}` | Atualiza o nome de uma categoria |
| `DELETE` | `/v1/categorias/{id}` | Remove uma categoria |

> Não é permitido remover uma categoria que possua subcategorias com lançamentos atrelados.  
> Ao remover uma categoria, suas subcategorias são removidas automaticamente (cascade).

---

### Subcategorias

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/subcategorias` | Lista subcategorias (filtros opcionais: `nome`, `id_subcategoria`, `page`, `size`) |
| `GET` | `/v1/subcategorias/{id}` | Busca uma subcategoria pelo ID |
| `POST` | `/v1/subcategorias` | Cria uma nova subcategoria |
| `PUT` | `/v1/subcategorias/{id}` | Atualiza uma subcategoria |
| `DELETE` | `/v1/subcategorias/{id}` | Remove uma subcategoria |

**Exemplo — POST `/v1/subcategorias`**

> Não é permitido remover uma subcategoria que possua lançamentos atrelados.

---

### Lançamentos

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/lancamentos` | Lista lançamentos (filtros opcionais: `id_subcategoria`, `data_inicio`, `data_fim`, `page`, `size`) |
| `GET` | `/v1/lancamentos/{id}` | Busca um lançamento pelo ID |
| `POST` | `/v1/lancamentos` | Cria um novo lançamento |
| `PUT` | `/v1/lancamentos/{id}` | Atualiza um lançamento |
| `DELETE` | `/v1/lancamentos/{id}` | Remove um lançamento |

> O campo `data` é opcional na criação — quando não informado, assume a data atual.  
> O campo `comentario` é opcional.  
> Datas devem estar no formato `dd/MM/yyyy`.

---

### Balanço

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/balanco` | Consulta o balanço geral de um período |

**Parâmetros:**

| Parâmetro | Obrigatório | Descrição |
|---|---|---|
| `data_inicio` | Sim | Data inicial do período (dd/MM/yyyy) |
| `data_fim` | Sim | Data final do período (dd/MM/yyyy) |
| `id_categoria` | Não | Filtra o balanço por categoria |

> O balanço é calculado em tempo real com base nos lançamentos existentes. Nenhum dado é gravado no banco.  
> `receita` = soma dos valores positivos | `despesa` = soma dos valores negativos (em módulo) | `saldo` = receita − despesa.

---

### Respostas de erro

Em caso de falha, a API retorna um JSON padronizado com `codigo` e `mensagem`:

```json
{
  "codigo": "erro_validacao",
  "mensagem": "O campo 'nome' é obrigatório"
}
```

### Códigos de status utilizados

| Status | Significado |
|---|---|
| `200` | Sucesso |
| `201` | Recurso criado |
| `204` | Recurso removido |
| `400` | Dados inválidos ou ausentes |
| `401` | api-key ausente ou inválida |
| `404` | Recurso não encontrado |
| `409` | Conflito — nome duplicado |
| `422` | Operação não permitida (ex: remover subcategoria com lançamentos) |

---

## Observabilidade

A aplicação utiliza o **Spring Actuator** para monitoramento. Os seguintes endpoints estão disponíveis **sem necessidade de api-key**:

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Retorna o status da aplicação (`UP` ou `DOWN`) |
| `GET /actuator/metrics` | Lista as métricas disponíveis |

**Exemplos de métricas úteis:**
```
GET /actuator/metrics/http.server.requests
GET /actuator/metrics/http.server.requests.active
GET /actuator/metrics/application.ready.time
GET /actuator/metrics/hikaricp.connections.active
```

---

## Deploy na nuvem (Railway)

A API está publicada gratuitamente no **Railway** com banco MySQL provisionado na nuvem.

### Acessar a API em produção

| Recurso | URL |
|---|---|
| API Base | `https://itau-finance-control-api-production.up.railway.app` |
| Swagger UI | `https://itau-finance-control-api-production.up.railway.app/swagger-ui.html` |
| Health | `https://itau-finance-control-api-production.up.railway.app/actuator/health` |

> Todas as requisições exigem o header `api-key: aXRhw7o=`.

---

### Como fazer o deploy no Railway (passo a passo)

1. Acesse [railway.app](https://railway.app) e faça login com sua conta GitHub
2. Clique em **New Project → Deploy from GitHub repo**
3. Selecione o repositório `itau-finance-control-api`
4. Clique em **Add Service → Database → MySQL** para provisionar o banco
5. Nas variáveis de ambiente do serviço da API, configure:

| Variável | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:mysql://<host_railway>:3306/<db_name>?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | _(copiar do painel MySQL do Railway)_ |
| `DB_PASSWORD` | _(copiar do painel MySQL do Railway)_ |
| `DB_DRIVER` | `com.mysql.cj.jdbc.Driver` |
| `API_KEY` | `aXRhw7o=` |

6. O Railway detecta o `Dockerfile` automaticamente e faz o build
7. Após o deploy, acesse a URL gerada pelo Railway

> O arquivo `railway.toml` na raiz do projeto já configura o builder, healthcheck e restart policy automaticamente.

---

## Testes

```bash
mvn test
```

O projeto possui cobertura de testes unitários de **99%**, sem uso de `@SpringBootTest` ou `@WebMvcTest` — todos os testes são unitários puros com JUnit 5 e Mockito.
