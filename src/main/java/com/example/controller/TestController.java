package com.example.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.model.ConfluencePage;
import com.example.service.ConfluenceIngestionService;

@RestController
@RequestMapping("/test")
public class TestController {

    private final WebClient confluenceWebClient;

    private final ConfluenceIngestionService confluenceIngestionService;

    public TestController(WebClient confluenceWebClient, ConfluenceIngestionService confluenceIngestionService) {
        this.confluenceWebClient = confluenceWebClient;
        this.confluenceIngestionService = confluenceIngestionService;
    }

    @GetMapping("/confluence/space")
    public String testConfluenceSpace() {
        return fetchConfluenceSpace();
    }

    private String fetchConfluenceSpace() {
        return confluenceWebClient.get()
                .uri("/rest/api/space")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
    @GetMapping("/confluence/pages")
    public List<ConfluencePage> getPages() {
        return confluenceIngestionService.fetchPages();  
    }

    @PostMapping("/confluence/ingest")
    public String ingestPages() {
        confluenceIngestionService.ingest();
        return "Ingestion started";
    }
    
}
