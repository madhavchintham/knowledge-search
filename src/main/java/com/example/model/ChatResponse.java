package com.example.model;

import java.util.List;

public record ChatResponse(String answer, List<String> sources) {
    
}
