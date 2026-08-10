package com.tororang.springai.knowledge.presentation;

import com.tororang.springai.knowledge.application.IndexDocumentFileUseCase;
import com.tororang.springai.knowledge.application.IndexDocumentUseCase;
import com.tororang.springai.knowledge.application.SearchKnowledgeUseCase;
import com.tororang.springai.knowledge.application.dto.IndexDocumentCommand;
import com.tororang.springai.knowledge.application.dto.IndexDocumentFileCommand;
import com.tororang.springai.knowledge.application.dto.RetrievedChunkResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KnowledgeController.class)
class KnowledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IndexDocumentUseCase indexDocumentUseCase;

    @MockitoBean
    private SearchKnowledgeUseCase searchKnowledgeUseCase;

    @MockitoBean
    private IndexDocumentFileUseCase indexDocumentFileUseCase;

    @Test
    void 문서_등록_요청시_201과_documentId를_반환한다() throws Exception {
        UUID documentId = UUID.randomUUID();
        when(indexDocumentUseCase.index(eq(new IndexDocumentCommand("제목", "본문 내용")))).thenReturn(documentId);

        mockMvc.perform(post("/api/knowledge/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"본문 내용\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentId").value(documentId.toString()));
    }

    @Test
    void title이나_content가_비어있으면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/knowledge/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"content\":\"본문 내용\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 파일_업로드로_문서_등록시_201과_documentId를_반환한다() throws Exception {
        UUID documentId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "dummy".getBytes());
        when(indexDocumentFileUseCase.index(any())).thenReturn(documentId);

        mockMvc.perform(multipart("/api/knowledge/documents/file")
                        .file(file)
                        .param("title", "제목"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentId").value(documentId.toString()));

        ArgumentCaptor<IndexDocumentFileCommand> captor = ArgumentCaptor.forClass(IndexDocumentFileCommand.class);
        verify(indexDocumentFileUseCase).index(captor.capture());
        assertThat(captor.getValue().title()).isEqualTo("제목");
        assertThat(captor.getValue().filename()).isEqualTo("report.pdf");
        assertThat(captor.getValue().fileContent()).isEqualTo("dummy".getBytes());
    }

    @Test
    void 파일_업로드시_title이_비어있으면_400을_반환한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "dummy".getBytes());

        mockMvc.perform(multipart("/api/knowledge/documents/file")
                        .file(file)
                        .param("title", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 파일_업로드시_파일이_비어있으면_400을_반환한다() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "report.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/api/knowledge/documents/file")
                        .file(emptyFile)
                        .param("title", "제목"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 지원하지_않는_확장자면_400을_반환한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "dummy".getBytes());
        when(indexDocumentFileUseCase.index(any())).thenThrow(new IllegalArgumentException("unsupported file type"));

        mockMvc.perform(multipart("/api/knowledge/documents/file")
                        .file(file)
                        .param("title", "제목"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 검색_요청시_200과_결과_목록을_반환한다() throws Exception {
        when(searchKnowledgeUseCase.search("질문"))
                .thenReturn(List.of(new RetrievedChunkResult("관련 내용", 0.9)));

        mockMvc.perform(post("/api/knowledge/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"질문\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].content").value("관련 내용"))
                .andExpect(jsonPath("$.results[0].score").value(0.9));
    }

    @Test
    void query가_비어있으면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/knowledge/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 검색_결과가_없으면_빈_목록을_반환한다() throws Exception {
        when(searchKnowledgeUseCase.search(any())).thenReturn(List.of());

        mockMvc.perform(post("/api/knowledge/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"질문\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isEmpty());
    }
}
