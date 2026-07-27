package com.tororang.springai.prompttemplate.presentation;

import com.tororang.springai.prompttemplate.application.RegisterPromptTemplateUseCase;
import com.tororang.springai.prompttemplate.application.RenderPromptTemplateUseCase;
import com.tororang.springai.prompttemplate.application.dto.RegisterPromptTemplateCommand;
import com.tororang.springai.prompttemplate.application.dto.RenderPromptTemplateCommand;
import com.tororang.springai.prompttemplate.domain.exception.DuplicateTemplateNameException;
import com.tororang.springai.prompttemplate.domain.exception.MissingTemplateVariableException;
import com.tororang.springai.prompttemplate.domain.exception.PromptTemplateNotFoundException;
import com.tororang.springai.prompttemplate.presentation.dto.RegisterPromptTemplateRequest;
import com.tororang.springai.prompttemplate.presentation.dto.RegisterPromptTemplateResponse;
import com.tororang.springai.prompttemplate.presentation.dto.RenderPromptTemplateRequest;
import com.tororang.springai.prompttemplate.presentation.dto.RenderPromptTemplateResponse;
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
@RequestMapping("/api/prompt-templates")
public class PromptTemplateController {

    private final RegisterPromptTemplateUseCase registerPromptTemplateUseCase;
    private final RenderPromptTemplateUseCase renderPromptTemplateUseCase;

    public PromptTemplateController(RegisterPromptTemplateUseCase registerPromptTemplateUseCase,
            RenderPromptTemplateUseCase renderPromptTemplateUseCase) {
        this.registerPromptTemplateUseCase = registerPromptTemplateUseCase;
        this.renderPromptTemplateUseCase = renderPromptTemplateUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterPromptTemplateResponse register(@RequestBody RegisterPromptTemplateRequest request) {
        if (request.name() == null || request.name().isBlank()
                || request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("name and content must not be blank");
        }

        UUID templateId = registerPromptTemplateUseCase.register(
                new RegisterPromptTemplateCommand(request.name(), request.content()));
        return new RegisterPromptTemplateResponse(templateId);
    }

    @PostMapping("/{name}/render")
    public RenderPromptTemplateResponse render(@PathVariable String name,
            @RequestBody RenderPromptTemplateRequest request) {
        String rendered = renderPromptTemplateUseCase.render(
                new RenderPromptTemplateCommand(name, request.variables()));
        return new RenderPromptTemplateResponse(rendered);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgument() {
    }

    @ExceptionHandler(MissingTemplateVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleMissingTemplateVariable() {
    }

    @ExceptionHandler(DuplicateTemplateNameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleDuplicateTemplateName() {
    }

    @ExceptionHandler(PromptTemplateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlePromptTemplateNotFound() {
    }
}
