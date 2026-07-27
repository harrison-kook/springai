package com.tororang.springai.member.presentation;

import com.tororang.springai.member.application.FindMemberUseCase;
import com.tororang.springai.member.application.RegisterMemberUseCase;
import com.tororang.springai.member.application.dto.MemberResult;
import com.tororang.springai.member.domain.exception.DuplicateEmailException;
import com.tororang.springai.member.domain.exception.MemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterMemberUseCase registerMemberUseCase;

    @MockitoBean
    private FindMemberUseCase findMemberUseCase;

    @Test
    void 회원_등록_요청시_201과_회원_id를_반환한다() throws Exception {
        UUID id = UUID.randomUUID();
        when(registerMemberUseCase.register(any())).thenReturn(id);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"chulsoo@example.com\",\"name\":\"철수\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(id.toString()));
    }

    @Test
    void 이메일_형식이_올바르지_않으면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\",\"name\":\"철수\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 이미_존재하는_이메일로_등록하면_409를_반환한다() throws Exception {
        when(registerMemberUseCase.register(any())).thenThrow(new DuplicateEmailException("duplicate"));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"chulsoo@example.com\",\"name\":\"철수\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void 회원_조회_요청시_200과_회원_정보를_반환한다() throws Exception {
        UUID id = UUID.randomUUID();
        when(findMemberUseCase.find(id)).thenReturn(new MemberResult(id, "chulsoo@example.com", "철수"));

        mockMvc.perform(get("/api/members/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(id.toString()))
                .andExpect(jsonPath("$.email").value("chulsoo@example.com"))
                .andExpect(jsonPath("$.name").value("철수"));
    }

    @Test
    void 존재하지_않는_id로_조회하면_404를_반환한다() throws Exception {
        UUID id = UUID.randomUUID();
        when(findMemberUseCase.find(id)).thenThrow(new MemberNotFoundException("not found"));

        mockMvc.perform(get("/api/members/{id}", id))
                .andExpect(status().isNotFound());
    }
}
