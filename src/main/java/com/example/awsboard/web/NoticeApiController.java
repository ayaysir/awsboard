package com.example.awsboard.web;

import com.example.awsboard.service.posts.NoticeService;
import com.example.awsboard.service.posts.PostsService;
import com.example.awsboard.web.dto.PostsResponseDTO;
import com.example.awsboard.web.dto.PostsSaveRequestDTO;
import com.example.awsboard.web.dto.PostsUpdateRequestDTO;
import com.example.awsboard.web.dto.notice.NoticeResponseDTO;
import com.example.awsboard.web.dto.notice.NoticeSaveRequestDTO;
import com.example.awsboard.web.dto.notice.NoticeUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class NoticeApiController {

    private final NoticeService noticeService;
    private final LogApiController logApiController;
    private final String DEFAULT_URI = "/api/v1/notice";

    @PostMapping(DEFAULT_URI)
    public Long save(@RequestBody NoticeSaveRequestDTO requestDTO) {
        return noticeService.save(requestDTO);
    }

    @PutMapping(DEFAULT_URI + "/{id}")
    public Long update(@PathVariable Long id, @RequestBody NoticeUpdateRequestDTO requestDTO) {
        return noticeService.update(id, requestDTO);
    }

    @GetMapping(DEFAULT_URI + "/{id}")
    public NoticeResponseDTO findById(@PathVariable Long id) {
        NoticeResponseDTO notice = noticeService.findById(id);
        notice.setViewCount(logApiController.getViewCount("notice", notice.getId()));
        return notice;
    }

    @DeleteMapping(DEFAULT_URI + "/{id}")
    public Long delete(@PathVariable Long id) {
        noticeService.delete(id);
        return id;
    }

}
