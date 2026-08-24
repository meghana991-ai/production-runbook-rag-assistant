# Production Runbook RAG Assistant

Level 01 retrieval-augmented generation project for answering operational questions from trusted runbooks.

## Current milestone

The first increment provides a Spring Boot API with explicit retrieval and answer-generation boundaries. It already enforces an important RAG rule: when retrieval finds no evidence, the service refuses to invent an answer.

### API

```http
POST /api/v1/questions
Content-Type: application/json

{"question":"How do I restart checkout-api?"}
```

```json
{
  "answer": "I could not find enough evidence in the indexed runbooks to answer that question.",
  "citations": [],
  "grounded": false
}
```

## Run locally

Requirements: Java 17+

```bash
cd backend
./mvnw spring-boot:run
```

Health check: `GET http://localhost:8080/actuator/health`

Run tests:

```bash
cd backend
./mvnw test
```

## Planned Level 01 increments

- PDF and text ingestion
- Deterministic chunking with document and page metadata
- Embedding generation
- Elasticsearch vector retrieval
- LLM answer generation using retrieved context only
- Source citations and retrieval evaluation
- Angular chat interface
