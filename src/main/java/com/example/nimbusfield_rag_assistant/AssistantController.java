package com.example.nimbusfield_rag_assistant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam("question") String question) {
        return ResponseEntity.ok(assistantService.ask(question));
    }
}