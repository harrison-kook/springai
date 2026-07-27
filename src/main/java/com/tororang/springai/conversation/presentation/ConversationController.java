package com.tororang.springai.conversation.presentation;

import com.tororang.springai.conversation.application.SendMessageUseCase;
import com.tororang.springai.conversation.application.StartConversationUseCase;
import com.tororang.springai.conversation.application.dto.ConversationResult;
import com.tororang.springai.conversation.application.dto.SendMessageCommand;
import com.tororang.springai.conversation.domain.exception.ConversationNotFoundException;
import com.tororang.springai.conversation.presentation.dto.ConversationResponse;
import com.tororang.springai.conversation.presentation.dto.SendMessageRequest;
import com.tororang.springai.conversation.presentation.dto.StartConversationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final StartConversationUseCase startConversationUseCase;
    private final SendMessageUseCase sendMessageUseCase;

    public ConversationController(StartConversationUseCase startConversationUseCase,
            SendMessageUseCase sendMessageUseCase) {
        this.startConversationUseCase = startConversationUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StartConversationResponse start() {
        UUID conversationId = startConversationUseCase.start();
        return new StartConversationResponse(conversationId);
    }

    @PostMapping("/{id}/messages")
    public ConversationResponse sendMessage(@PathVariable UUID id, @RequestBody SendMessageRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        ConversationResult result = sendMessageUseCase.send(new SendMessageCommand(id, request.content()));
        return ConversationResponse.from(result);
    }

    @ExceptionHandler(ConversationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleConversationNotFound() {
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgument() {
    }
}
