package com.tororang.springai.prompttemplate.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PromptTemplatePageController {

    @GetMapping("/prompt-templates")
    public String promptTemplatePage() {
        return "prompt-templates";
    }
}
