package com.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.ConfluenceIngestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ingestion")
@RequiredArgsConstructor
@Slf4j
public class IngestionController {

    private final ConfluenceIngestionService confluenceIngestionService;

    @PostMapping
    public String ingestPages() {
        log.info("Ingestion request received.");
        confluenceIngestionService.ingest();
        log.info("Ingestion process completed.");
        return "Ingestion has been completed.";
    }
    
}
