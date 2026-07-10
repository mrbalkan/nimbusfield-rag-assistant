package com.example.nimbusfield_rag_assistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import org.springframework.ai.document.Document;

@Component
    public class HandbookIngestion implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(HandbookIngestion.class);

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final Resource[] handbook;

    public HandbookIngestion(VectorStore vectorStore, JdbcTemplate jdbcTemplate,
                             @Value("classpath:docs/*.md") Resource[] handbook) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.handbook = handbook;
    }

    @Override
    public void run(ApplicationArguments args) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from vector_store", Integer.class);
        if (count != null && count > 0) {
            log.info("Vector store already contains {} chunks, skipping ingestion", count);
            return;
        }

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(300)
                .build();

        for (Resource resource : handbook) {
            List<Document> documents = new TikaDocumentReader(resource).get();
            documents.forEach(doc ->
                    doc.getMetadata().put("source", resource.getFilename()));

            List<Document> chunks = splitter.apply(documents);
            vectorStore.add(chunks);

            log.info("Ingested {} chunks from {}", chunks.size(), resource.getFilename());
        }
    }
}