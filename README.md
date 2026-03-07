# Finance Control API

API de controle financeiro desenvolvida com Java 17, Spring Boot 3 e arquitetura hexagonal.

## Tecnologias
- Java 17
- Spring Boot 3.3.2
- Maven

## Como rodar
```bash
mvn spring-boot:run
```

## Como testar
```bash
mvn test
```

## Endpoints

### Health
```
GET /v1/health
```
## Estrutura de pacotes

```
com.financecontrol.api
├── adapters
│   └── controller     → controllers HTTP (entrada)
├── application
│   └── usecase        → contratos dos casos de uso
├── domain
│   └── shared         → exceções e abstrações do domínio
└── infrastructure
    └── config         → configurações técnicas
```

