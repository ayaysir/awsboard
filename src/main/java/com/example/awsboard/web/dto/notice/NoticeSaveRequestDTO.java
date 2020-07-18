package com.example.awsboard.web.dto.notice;

import com.example.awsboard.domain.notice.Notice;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeSaveRequestDTO {
    private String title;
    private String content;
    private String author;
    private Long authorId;

    @Builder
    public NoticeSaveRequestDTO(String title, String content, String author, Long authorId) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
    }

    public Notice toEntity() {
        return Notice.builder()
                .title(title)
                .content(content)
                .author(author)
                .authorId(authorId)
                .build();
    }

}
