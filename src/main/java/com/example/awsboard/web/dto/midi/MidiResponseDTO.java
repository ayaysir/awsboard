package com.example.awsboard.web.dto.midi;

import com.example.awsboard.domain.midi.Midi;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class MidiResponseDTO {

    private Long id;

    private Long userId;    // 업로드한 사람

    private String category;

    private String customTitle;

    private String hash;

    private String originalMidiPath;

    private String originalMp3Path;

    private String originalFileName;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public MidiResponseDTO(Midi entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.category = entity.getCategory();
        this.customTitle = entity.getCustomTitle();
        this.hash = entity.getHash();
        this.originalMidiPath = entity.getOriginalMidiPath();
        this.originalMp3Path = entity.getOriginalMp3Path();
        this.originalFileName = entity.getOriginalFileName();
        this.createdDate = entity.getCreatedDate();
        this.modifiedDate = entity.getModifiedDate();
    }
}
