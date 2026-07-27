package com.tororang.springai.member.presentation;

import com.tororang.springai.member.application.FindMemberUseCase;
import com.tororang.springai.member.application.RegisterMemberUseCase;
import com.tororang.springai.member.application.dto.MemberResult;
import com.tororang.springai.member.application.dto.RegisterMemberCommand;
import com.tororang.springai.member.domain.exception.DuplicateEmailException;
import com.tororang.springai.member.domain.exception.MemberNotFoundException;
import com.tororang.springai.member.presentation.dto.MemberResponse;
import com.tororang.springai.member.presentation.dto.RegisterMemberRequest;
import com.tororang.springai.member.presentation.dto.RegisterMemberResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final RegisterMemberUseCase registerMemberUseCase;
    private final FindMemberUseCase findMemberUseCase;

    public MemberController(RegisterMemberUseCase registerMemberUseCase, FindMemberUseCase findMemberUseCase) {
        this.registerMemberUseCase = registerMemberUseCase;
        this.findMemberUseCase = findMemberUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterMemberResponse register(@RequestBody RegisterMemberRequest request) {
        if (request.email() == null || request.email().isBlank() || !request.email().contains("@")) {
            throw new IllegalArgumentException("email must be a valid, non-empty address");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        UUID memberId = registerMemberUseCase.register(new RegisterMemberCommand(request.email(), request.name()));
        return new RegisterMemberResponse(memberId);
    }

    @GetMapping("/{id}")
    public MemberResponse find(@PathVariable UUID id) {
        MemberResult result = findMemberUseCase.find(id);
        return MemberResponse.from(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgument() {
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleDuplicateEmail() {
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleMemberNotFound() {
    }
}
