package com.tororang.springai.knowledge.application;

import com.tororang.springai.knowledge.application.dto.IndexDocumentFileCommand;
import com.tororang.springai.knowledge.domain.DocumentChunker;
import com.tororang.springai.knowledge.domain.DocumentContentExtractor;
import com.tororang.springai.knowledge.domain.DocumentFileType;
import com.tororang.springai.knowledge.domain.EmbeddedChunk;
import com.tororang.springai.knowledge.domain.EmbeddingGenerator;
import com.tororang.springai.knowledge.domain.EmbeddingVector;
import com.tororang.springai.knowledge.domain.KnowledgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexDocumentFileUseCaseTest {

    @Mock
    private DocumentContentExtractor contentExtractor;

    @Mock
    private EmbeddingGenerator embeddingGenerator;

    @Mock
    private KnowledgeRepository knowledgeRepository;

    private IndexDocumentFileUseCase useCase;

    @BeforeEach
    void setUp() {
        IndexDocumentUseCase indexDocumentUseCase =
                new IndexDocumentUseCase(new DocumentChunker(), embeddingGenerator, knowledgeRepository, 5);
        useCase = new IndexDocumentFileUseCase(contentExtractor, indexDocumentUseCase);
    }

    @Test
    void 파일을_색인하면_추출된_텍스트가_청크로_분할되어_저장된다() {
        byte[] fileContent = "file-bytes".getBytes();
        when(contentExtractor.extract(DocumentFileType.PDF, fileContent)).thenReturn("1234567890");
        when(embeddingGenerator.generateAll(anyList()))
                .thenReturn(List.of(new EmbeddingVector(List.of(0.1f)), new EmbeddingVector(List.of(0.2f))));

        useCase.index(new IndexDocumentFileCommand("제목", "report.pdf", fileContent));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EmbeddedChunk>> captor = ArgumentCaptor.forClass(List.class);
        verify(knowledgeRepository).save(captor.capture());
        List<EmbeddedChunk> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).chunk().content()).isEqualTo("12345");
        assertThat(saved.get(1).chunk().content()).isEqualTo("67890");
    }

    @Test
    void 추출기에는_파일명으로_판별한_파일타입과_파일내용이_그대로_전달된다() {
        byte[] fileContent = "sheet-bytes".getBytes();
        when(contentExtractor.extract(eq(DocumentFileType.XLSX), eq(fileContent))).thenReturn("내용");
        when(embeddingGenerator.generateAll(anyList())).thenReturn(List.of(new EmbeddingVector(List.of(0.1f))));

        useCase.index(new IndexDocumentFileCommand("제목", "sheet.xlsx", fileContent));

        verify(contentExtractor).extract(DocumentFileType.XLSX, fileContent);
    }

    @Test
    void 지원하지_않는_확장자면_예외가_발생하고_추출기와_저장소는_호출되지_않는다() {
        assertThatThrownBy(() -> useCase.index(new IndexDocumentFileCommand("제목", "image.png", new byte[0])))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(contentExtractor);
        verify(knowledgeRepository, never()).save(anyList());
    }

    @Test
    void 색인된_문서의_id가_반환된다() {
        when(contentExtractor.extract(eq(DocumentFileType.MD), eq(new byte[0]))).thenReturn("내용");
        when(embeddingGenerator.generateAll(anyList())).thenReturn(List.of(new EmbeddingVector(List.of(0.1f))));

        UUID documentId = useCase.index(new IndexDocumentFileCommand("제목", "notes.md", new byte[0]));

        assertThat(documentId).isNotNull();
    }
}