# Production Runbook RAG Assistant

Level 01 retrieval-augmented generation project for answering operational questions from trusted runbooks.

## Current milestone

The service provides Spring Boot APIs for ingesting PDF/text runbooks and asking grounded questions. Uploaded documents are extracted page by page, split into deterministic overlapping chunks, embedded, and stored with source metadata. Development defaults to reproducible local embeddings, in-memory cosine retrieval, and an extractive context-only answer. OpenAI embeddings and grounded LLM answers can be enabled through environment configuration.

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

Start local Elasticsearch:

```bash
docker compose up -d
```

Run the backend with Elasticsearch dense-vector storage and k-NN retrieval:

```bash
cd backend
SPRING_PROFILES_ACTIVE=elasticsearch ./mvnw spring-boot:run
```

The service creates the `runbook-chunks` index automatically with a 256-dimension `dense_vector` field and cosine similarity. Override the connection with `ELASTICSEARCH_URL` and the index name with `ELASTICSEARCH_INDEX`.

Enable OpenAI embeddings (never commit the key):

```bash
export OPENAI_API_KEY="your-key"
export RAG_EMBEDDING_PROVIDER="openai"
```

Enable OpenAI answer generation using the Responses API:

```bash
export OPENAI_API_KEY="your-key"
export RAG_ANSWER_PROVIDER="openai"
# Optional: export OPENAI_ANSWER_MODEL="gpt-5-mini"
```

The answer prompt treats retrieved text as untrusted data, prohibits outside knowledge, requires `[n]` chunk citations, and refuses to answer when the supplied context is insufficient. API responses also include document/page citation metadata.

Run tests:

```bash
cd backend
./mvnw test
```

## Planned Level 01 increments

- [x] PDF and text ingestion
- [x] Deterministic chunking with document and page metadata
- [x] Embedding generation
- [x] Elasticsearch vector retrieval
- [x] LLM answer generation using retrieved context only
- [x] Source citations and insufficient-context refusal
- Retrieval evaluation
- Angular chat interface
