package com.tororang.springai.conversation.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatPageController {

    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }
}
