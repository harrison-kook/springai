package com.tororang.springai.prompttemplate.presentation;

import com.tororang.springai.prompttemplate.application.RegisterPromptTemplateUseCase;
import com.tororang.springai.prompttemplate.application.RenderPromptTemplateUseCase;
import com.tororang.springai.prompttemplate.application.dto.RenderPromptTemplateCommand;
import com.tororang.springai.prompttemplate.domain.exception.DuplicateTemplateNameException;
import com.tororang.springai.prompttemplate.domain.exception.MissingTemplateVariableException;
import com.tororang.springai.prompttemplate.domain.exception.PromptTemplateNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromptTemplateController.class)
class PromptTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterPromptTemplateUseCase registerPromptTemplateUseCase;

    @MockitoBean
    private RenderPromptTemplateUseCase renderPromptTemplateUseCase;

    @Test
    void 템플릿_등록_요청시_201과_템플릿_id를_반환한다() throws Exception {
        UUID id = UUID.randomUUID();
        when(registerPromptTemplateUseCase.register(any())).thenReturn(id);

        mockMvc.perform(post("/api/prompt-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"greeting\",\"content\":\"안녕 {{name}}\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.templateId").value(id.toString()));
    }

    @Test
    void 이름이나_content가_비어있으면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/prompt-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"content\":\"안녕 {{name}}\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 이미_존재하는_이름으로_등록하면_409를_반환한다() throws Exception {
        when(registerPromptTemplateUseCase.register(any()))
                .thenThrow(new DuplicateTemplateNameException("duplicate"));

        mockMvc.perform(post("/api/prompt-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"greeting\",\"content\":\"안녕 {{name}}\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void 렌더링_요청시_200과_렌더링된_문자열을_반환한다() throws Exception {
        when(renderPromptTemplateUseCase.render(eq(new RenderPromptTemplateCommand("greeting", Map.of("name", "철수")))))
                .thenReturn("안녕 철수님");

        mockMvc.perform(post("/api/prompt-templates/{name}/render", "greeting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"variables\":{\"name\":\"철수\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rendered").value("안녕 철수님"));
    }

    @Test
    void 존재하지_않는_템플릿_이름으로_렌더링하면_404를_반환한다() throws Exception {
        when(renderPromptTemplateUseCase.render(any()))
                .thenThrow(new PromptTemplateNotFoundException("not found"));

        mockMvc.perform(post("/api/prompt-templates/{name}/render", "unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"variables\":{}}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 변수가_누락된_렌더링_요청시_400을_반환한다() throws Exception {
        when(renderPromptTemplateUseCase.render(any()))
                .thenThrow(new MissingTemplateVariableException("missing: name"));

        mockMvc.perform(post("/api/prompt-templates/{name}/render", "greeting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"variables\":{}}"))
                .andExpect(status().isBadRequest());
    }
}
