package com.example.awsboard.web.dto.midi;

import com.example.awsboard.domain.midi.Midi;

public class MidiResponseDTO {

    private Long id;

    private Long userId;    // 업로드한 사람

    private String category;

    private String customTitle;

    private String hash;

    private String originalMidiPath;

    private String originalMp3Path;

    public MidiResponseDTO(Midi entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.category = entity.getCategory();
        this.customTitle = entity.getCustomTitle();
        this.hash = entity.getHash();
        this.originalMidiPath = entity.getOriginalMidiPath();
        this.originalMp3Path = entity.getOriginalMp3Path();
    }
}
