package com.example.awsboard.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsUpdateRequestDTO {

    private String title, content;

    @Builder
    public PostsUpdateRequestDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
