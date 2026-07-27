package com.tororang.springai.prompttemplate.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PromptTemplatePageController.class)
class PromptTemplatePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 프롬프트_템플릿_페이지_요청시_200과_prompt_templates_뷰를_반환한다() throws Exception {
        mockMvc.perform(get("/prompt-templates"))
                .andExpect(status().isOk())
                .andExpect(view().name("prompt-templates"));
    }
}
