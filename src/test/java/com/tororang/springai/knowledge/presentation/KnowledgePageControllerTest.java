package com.tororang.springai.knowledge.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(KnowledgePageController.class)
class KnowledgePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 지식_페이지_요청시_200과_knowledge_뷰를_반환한다() throws Exception {
        mockMvc.perform(get("/knowledge"))
                .andExpect(status().isOk())
                .andExpect(view().name("knowledge"));
    }
}
