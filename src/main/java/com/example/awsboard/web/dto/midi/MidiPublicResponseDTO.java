package com.example.awsboard.web.dto.midi;

import com.example.awsboard.domain.midi.Midi;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class MidiPublicResponseDTO {

    private Long id;

    private Long userId;    // 업로드한 사람

    private String category;

    private String customTitle;

    private String originalFileName;

    @Builder
    public MidiPublicResponseDTO(Midi entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.category = entity.getCategory();
        this.customTitle = entity.getCustomTitle();
        this.originalFileName = entity.getOriginalFileName();
    }
}
