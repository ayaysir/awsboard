package com.example.awsboard.web.dto.notice;

import com.example.awsboard.domain.notice.Notice;
import lombok.Getter;
import lombok.Setter;

@Getter
public class NoticeResponseDTO {

    private Long id, authorId;
    private String title, content, author;

    @Setter
    private Long viewCount;

    public NoticeResponseDTO(Notice entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
        this.authorId = entity.getAuthorId();
    }

}
