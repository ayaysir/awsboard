package com.example.awsboard.service.posts;

import com.example.awsboard.domain.posts.PostsRepository;
import com.example.awsboard.web.dto.PostsSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDTO requestDTO) {
        return postsRepository.save(requestDTO.toEntity())
                .getId();
    }
}
