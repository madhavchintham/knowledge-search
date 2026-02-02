package com.example.service;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public ChatService(VectorStore vectorStore, ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    private List<Document> getSimilarDocuments(String query) {
        SearchRequest searchRequest = SearchRequest.builder().query(query).topK(5).build();
        return vectorStore.similaritySearch(searchRequest);
    }

    public String chat(String query) {
        List<Document> similarDocuments = getSimilarDocuments(query);
        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        Prompt prompt = new Prompt("""
            You are an assistant answering questions using internal company documents.

            Answer ONLY using the information below.
            If the answer is not present, say "I don't know".

            Context:
            %s

            Question:
            %s
            """.formatted(context, query));

        return chatModel.call(prompt).getResult().getOutput().getText();
    }
    
}
