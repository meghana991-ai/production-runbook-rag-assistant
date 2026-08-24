# Production Runbook RAG Assistant

Level 01 retrieval-augmented generation project for answering operational questions from trusted runbooks.

## Current milestone

The service provides Spring Boot APIs for ingesting PDF/text runbooks and asking grounded questions. Uploaded documents are extracted page by page, normalized, split into deterministic overlapping chunks, and stored with source metadata. The current in-memory store is an adapter that will be replaced by Elasticsearch in the next increment.

### API

Upload a document:

```bash
curl -F "file=@checkout-runbook.pdf" http://localhost:8080/api/v1/documents
```

Supported formats: PDF and UTF-8 plain text. Maximum upload size: 10 MB.

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

- [x] PDF and text ingestion
- [x] Deterministic chunking with document and page metadata
- Embedding generation
- Elasticsearch vector retrieval
- LLM answer generation using retrieved context only
- Source citations and retrieval evaluation
- Angular chat interface
