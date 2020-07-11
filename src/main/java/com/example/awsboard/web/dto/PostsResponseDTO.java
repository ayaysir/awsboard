package com.example.awsboard.web.dto;

import com.example.awsboard.domain.posts.Posts;
import lombok.Getter;

@Getter
public class PostsResponseDTO {

    private Long id;
    private String title, content, author;

    public PostsResponseDTO(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getAuthor();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }



}
