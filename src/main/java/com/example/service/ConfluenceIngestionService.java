package com.example.service;

import java.util.List;

import org.jsoup.Jsoup;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.model.ConfluencePage;
import com.example.model.ConfluencePageResponse;
import com.example.model.PageResult;

@Service
public class ConfluenceIngestionService {

    private final WebClient confluenceWebClient;
    private final TokenTextSplitter textSplitter;
    private final VectorStore vectorStore;
    @Value("${confluence.space-id}")
    private final String spaceId;

    public ConfluenceIngestionService(WebClient confluenceWebClient,
            TokenTextSplitter textSplitter,
            VectorStore vectorStore,
            @Value("${confluence.space-id}") String spaceId) {
        this.confluenceWebClient = confluenceWebClient;
        this.spaceId = spaceId;
        this.textSplitter = textSplitter;
        this.vectorStore = vectorStore;
    }

    public List<ConfluencePage> fetchPages() {
        ConfluencePageResponse response = confluenceWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v2/pages")
                        .queryParam("space-id", spaceId)
                        .queryParam("limit", 25)
                        .queryParam("body-format", "storage")
                        .build())
                .retrieve()
                .bodyToMono(ConfluencePageResponse.class)
                .block();
        if (response == null || response.results() == null || response.results().isEmpty()) {
            System.out.println("No pages found in the specified space.");
            return List.of();
        }
        return response.results().stream()
                .map(this::toDomain)
                .toList();
    }

    private ConfluencePage toDomain(PageResult page) {
        ConfluencePage confluencePage = new ConfluencePage(
                page.id(),
                page.title(),
                page.body().storage().value(),
                page.spaceId(),
                page._links().get("webui"));
        return confluencePage;
    }

    public void ingest() {
        List<ConfluencePage> pages = fetchPages();
        for (ConfluencePage page : pages) {
            String cleanText = Jsoup.parse(page.htmlContent()).text();
            if (cleanText.isBlank()) {
                continue;
            }
            Document doc = new Document(cleanText);
            List<Document> chunks = textSplitter.split(doc);
            vectorStore.add(chunks);
        }
    }

}
