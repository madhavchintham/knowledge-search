package com.example.model;

import java.util.Map;

public record PageResult(String id, String title, String spaceId, PageBody body, Map<String, String> _links) {
    
}
