package com.example.jai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class PromptRequest {
    private String message;
    private String model;

}
