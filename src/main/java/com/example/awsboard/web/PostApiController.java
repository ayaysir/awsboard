package com.example.awsboard.web;

import com.example.awsboard.service.posts.PostsService;
import com.example.awsboard.web.dto.PostsSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostsService postsService;

    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDTO requestDTO) {
        return postsService.save(requestDTO);
    }

}
