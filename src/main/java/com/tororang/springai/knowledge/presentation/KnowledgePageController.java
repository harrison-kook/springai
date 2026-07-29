package com.tororang.springai.knowledge.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KnowledgePageController {

    @GetMapping("/knowledge")
    public String knowledgePage() {
        return "knowledge";
    }
}
