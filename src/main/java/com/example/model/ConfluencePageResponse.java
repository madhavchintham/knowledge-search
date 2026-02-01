package com.example.model;

import java.util.List;


public record ConfluencePageResponse(List<PageResult> results, Link links) {
    
}
