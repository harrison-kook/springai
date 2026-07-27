package com.tororang.springai.conversation.presentation;

import com.tororang.springai.conversation.application.SendMessageUseCase;
import com.tororang.springai.conversation.application.StartConversationUseCase;
import com.tororang.springai.conversation.application.dto.ConversationResult;
import com.tororang.springai.conversation.application.dto.SendMessageCommand;
import com.tororang.springai.conversation.domain.Message;
import com.tororang.springai.conversation.domain.MessageRole;
import com.tororang.springai.conversation.domain.exception.ConversationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartConversationUseCase startConversationUseCase;

    @MockitoBean
    private SendMessageUseCase sendMessageUseCase;

    @Test
    void 대화_시작_요청시_201과_대화_id를_반환한다() throws Exception {
        UUID id = UUID.randomUUID();
        when(startConversationUseCase.start()).thenReturn(id);

        mockMvc.perform(post("/api/conversations"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conversationId").value(id.toString()));
    }

    @Test
    void 메시지_전송_요청시_200과_사용자_메시지와_AI_응답을_반환한다() throws Exception {
        UUID id = UUID.randomUUID();
        Message userMessage = new Message(MessageRole.USER, "안녕하세요");
        Message aiMessage = new Message(MessageRole.AI, "반갑습니다");
        when(sendMessageUseCase.send(eq(new SendMessageCommand(id, "안녕하세요"))))
                .thenReturn(new ConversationResult(id, List.of(userMessage, aiMessage)));

        mockMvc.perform(post("/api/conversations/{id}/messages", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"안녕하세요\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value(id.toString()))
                .andExpect(jsonPath("$.messages[0].role").value("USER"))
                .andExpect(jsonPath("$.messages[0].content").value("안녕하세요"))
                .andExpect(jsonPath("$.messages[1].role").value("AI"))
                .andExpect(jsonPath("$.messages[1].content").value("반갑습니다"));
    }

    @Test
    void 존재하지_않는_대화_id로_요청하면_404를_반환한다() throws Exception {
        UUID id = UUID.randomUUID();
        when(sendMessageUseCase.send(any())).thenThrow(new ConversationNotFoundException("not found: " + id));

        mockMvc.perform(post("/api/conversations/{id}/messages", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"안녕하세요\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 빈_content로_요청하면_400을_반환한다() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/api/conversations/{id}/messages", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
