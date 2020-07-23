package com.example.awsboard.web.dto.log;

import com.example.awsboard.domain.log.Log;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogSaveRequestDTO {

    private String boardName;
    private Long articleId, userId;

    @Builder
    public LogSaveRequestDTO(String boardName, Long articleId, Long userId) {
        this.boardName = boardName;
        this.articleId = articleId;
        this.userId = userId;
    }

    public Log toEntity() {
        return Log.builder()
                .boardName(boardName)
                .articleId(articleId)
                .userId(userId)
                .build();
    }
}
