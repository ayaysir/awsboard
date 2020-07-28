package com.example.awsboard.web.dto;

import com.example.awsboard.domain.posts.Posts;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PostsResponseDTO {

    private Long id, authorId;
    private String title, content, author;

    @Setter
    private Long viewCount;

    public PostsResponseDTO(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
        this.authorId = entity.getAuthorId();
    }

}
