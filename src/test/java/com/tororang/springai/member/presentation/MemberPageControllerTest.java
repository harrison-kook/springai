package com.tororang.springai.member.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(MemberPageController.class)
class MemberPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 회원_페이지_요청시_200과_members_뷰를_반환한다() throws Exception {
        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("members"));
    }
}
