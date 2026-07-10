# Nimbusfield RAG Assistant

A small, complete Retrieval Augmented Generation (RAG) service built with **Spring AI 2.0**, **Anthropic Claude**, and **PostgreSQL/PGvector**.

It answers employee questions about a fictional company handbook (Nimbusfield Systems), grounding every answer in the actual document content instead of the model's training data. Full write-up: *[link to the DZone article once published]*.

## What it demonstrates

- A plain `/ask` endpoint backed by Claude, with no retrieval, and its honest "I don't know" type baseline
- Ingesting Markdown documents into PGvector at startup: read, chunk, embed, store
- Attaching Spring AI's `QuestionAnswerAdvisor` to a `ChatClient` for grounded, retrieval-augmented answers

## Prerequisites

- Java 21
- Docker Desktop, running
- An [Anthropic API key](https://console.anthropic.com/)

You do not need Maven installed separately. This project includes the Maven Wrapper (mvnw for macOS/Linux, mvnw.cmd for Windows) — a small script checked into the repo that downloads the exact right Maven version on first use and runs it for you. Every mvn ... command below is written as ./mvnw ... for this reason; just run it as shown and it handles Maven itself.

## Setup

**1. Clone and enter the project:**

```shell
git clone <this-repo-url>
cd nimbusfield-rag-assistant
```

**2. Export your API key:**

```shell
export ANTHROPIC_API_KEY=sk-ant-...
```

**3. Download the embedding model files.** These are intentionally not committed to the repo (see below), so this step is required before the first run:

```shell
mkdir -p src/main/resources/onnx/all-MiniLM-L6-v2

curl -fL -o src/main/resources/onnx/all-MiniLM-L6-v2/tokenizer.json \
  https://raw.githubusercontent.com/spring-projects/spring-ai/main/models/spring-ai-transformers/src/main/resources/onnx/all-MiniLM-L6-v2/tokenizer.json

curl -fL --http1.1 -o src/main/resources/onnx/all-MiniLM-L6-v2/model.onnx \
  https://media.githubusercontent.com/media/spring-projects/spring-ai/main/models/spring-ai-transformers/src/main/resources/onnx/all-MiniLM-L6-v2/model.onnx
```

Verify `model.onnx` is ~90 MB (not a few hundred bytes) before proceeding - see the article's "The Embedding Model" section for why this occasionally needs a retry.

**4. Run it:**

```shell
./mvnw spring-boot:run
```

Docker Compose starts a `pgvector/pgvector:pg17` container automatically on first run; no manual database setup needed.

**5. Try it:**

```shell
curl -G "http://localhost:8080/ask" --data-urlencode "question=What is the daily meal allowance for business trips in Europe?"
```

## Project structure

```
src/main/java/com/example/nimbusfield_rag_assistant/
├── NimbusfieldRagAssistantApplication.java
├── AssistantController.java   # REST endpoint (/ask)
├── AssistantService.java      # ChatClient + retrieval advisor wiring
└── HandbookIngestion.java     # Startup ingestion pipeline

src/main/resources/
├── application.properties
└── docs/                      # The fictional company handbook (source documents)
```

## License

MIT — see [LICENSE](LICENSE).
