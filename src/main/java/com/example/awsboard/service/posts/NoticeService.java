package com.example.awsboard.service.posts;

import com.example.awsboard.domain.notice.Notice;
import com.example.awsboard.domain.notice.NoticeRepository;
import com.example.awsboard.web.dto.notice.NoticeListResponseDTO;
import com.example.awsboard.web.dto.notice.NoticeResponseDTO;
import com.example.awsboard.web.dto.notice.NoticeSaveRequestDTO;
import com.example.awsboard.web.dto.notice.NoticeUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public Long save(NoticeSaveRequestDTO requestDTO) {
        return noticeRepository.save(requestDTO.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, NoticeUpdateRequestDTO requestDTO) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id=" + id));
        notice.update(requestDTO.getTitle(), requestDTO.getContent());

        return id;
    }

    public NoticeResponseDTO findById(Long id) {
        Notice entity = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id=" + id));

        return new NoticeResponseDTO(entity);

    }

    @Transactional(readOnly = true)
    public List<NoticeListResponseDTO> findAllDesc() {
        return noticeRepository.findAllDesc().stream()
                .map(NoticeListResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id=" + id));

        noticeRepository.delete(notice);
    }
}
