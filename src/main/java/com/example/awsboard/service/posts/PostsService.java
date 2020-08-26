package com.example.awsboard.service.posts;

import com.example.awsboard.domain.posts.Posts;
import com.example.awsboard.domain.posts.PostsRepository;
import com.example.awsboard.web.dto.PostsListResponseDTO;
import com.example.awsboard.web.dto.PostsResponseDTO;
import com.example.awsboard.web.dto.PostsSaveRequestDTO;
import com.example.awsboard.web.dto.PostsUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDTO requestDTO) {
        return postsRepository.save(requestDTO.toEntity())
                .getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDTO requestDTO) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id=" + id));
        posts.update(requestDTO.getTitle(), requestDTO.getContent());

        return id;
    }

    public PostsResponseDTO findById(Long id) {
        Posts entity = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id=" + id));

        return new PostsResponseDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDTO> findAllDesc() {
        return postsRepository.findAllDesc().stream().map(PostsListResponseDTO::new).collect(Collectors.toList());
    }

    // 페이지로 가져오기
    @Transactional(readOnly = true)
    public List<PostsListResponseDTO> findAllByOrderByIdDesc(Integer pageNum, Integer postsPerPage) {
        Page<Posts> page = postsRepository.findAll(
                // PageRequest의 page는 0부터 시작
                PageRequest.of(pageNum - 1, postsPerPage,
                        Sort.by(Sort.Direction.DESC, "id")
        ));
        return page.stream()
                .map(PostsListResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Long count() {
        return postsRepository.count();
    }

    @Transactional
    public void delete(Long id) {
        Posts post = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id=" + id));

        postsRepository.delete(post);

    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDTO> searchTitleAndContent(String keyword) {
        return postsRepository
                .findByContentContainingIgnoreCaseOrTitleContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(PostsListResponseDTO::new)
                .collect(Collectors.toList());
    }
}
