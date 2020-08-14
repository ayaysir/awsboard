package com.example.awsboard.web.dto.midi;

import com.example.awsboard.domain.midi.Midi;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class MidiRequestDTO {

    private Long userId;

    private String category;

    private String customTitle;

    @Setter
    private String hash;

    @Setter
    private String originalMidiPath;

    @Setter
    private String originalMp3Path;

    @Setter
    private String originalFileName;

    @Builder
    public MidiRequestDTO(Long userId, String category, String customTitle,
                          String hash, String originalMidiPath, String originalMp3Path,
                          String originalFileName) {
        this.userId = userId;
        this.category = category;
        this.customTitle = customTitle;
        this.hash = hash;
        this.originalMidiPath = originalMidiPath;
        this.originalMp3Path = originalMp3Path;
        this.originalFileName = originalFileName;
    }

    public Midi toEnity() {
        return Midi.builder()
                .userId(userId)
                .category(category)
                .customTitle(customTitle)
                .hash(hash)
                .originalMidiPath(originalMidiPath)
                .originalMp3Path(originalMp3Path)
                .originalFileName(originalFileName)
                .build();
    }

}
