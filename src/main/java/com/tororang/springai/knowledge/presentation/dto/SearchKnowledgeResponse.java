package com.tororang.springai.knowledge.presentation.dto;

import com.tororang.springai.knowledge.application.dto.RetrievedChunkResult;

import java.util.List;

public record SearchKnowledgeResponse(List<ChunkResponse> results) {

    public static SearchKnowledgeResponse from(List<RetrievedChunkResult> results) {
        return new SearchKnowledgeResponse(results.stream().map(ChunkResponse::from).toList());
    }

    public record ChunkResponse(String content, double score) {

        public static ChunkResponse from(RetrievedChunkResult result) {
            return new ChunkResponse(result.content(), result.score());
        }
    }
}
