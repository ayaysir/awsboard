package com.example.awsboard.web;

import com.example.awsboard.domain.midi.Midi;
import com.example.awsboard.service.posts.MidiService;
import com.example.awsboard.util.TimidityRunner;
import com.example.awsboard.web.dto.midi.MidiRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MidiApiController {

    private final MidiService midiService;
    private final String DEFAULT_URI = "/api/v1/midi";

    @PostMapping(DEFAULT_URI)
    public Long save(MidiRequestDTO requestDTO, @RequestParam("midi-file") MultipartFile files) throws Exception {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi/original";
        String filePath = basePath + "/" + files.getOriginalFilename();
        files.transferTo(new File(filePath));

        Authentication user = SecurityContextHolder.getContext().getAuthentication();

        String userId = user.getName();
        requestDTO.setHash("blahblah");
        requestDTO.setOriginalMidiPath(filePath);
        // convert to mp3
        requestDTO.setOriginalMp3Path("mp3path");
        return midiService.save(requestDTO);

    }

    @PostMapping(DEFAULT_URI + "/test")
    public String prototype(@RequestParam("midi-file") MultipartFile files) throws Exception {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi/original";

        File baseDir = new File(basePath);
        if(!baseDir.exists()) {
            baseDir.mkdirs();
            System.out.println("mkdirs");
        }

        String filePath = basePath + "/" + files.getOriginalFilename();

        System.out.println(files.getOriginalFilename());

        File file = new File(filePath);
        files.transferTo(file);

        return TimidityRunner.getHash(filePath);

    }

    @PostMapping(DEFAULT_URI + "/test2")
    public List<Boolean> midiPrototype2(@RequestParam("files") List<MultipartFile> files, MidiRequestDTO requestDTO) throws Exception {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi";

        File originalDir = new File(basePath + "/original");
        File mp3Dir = new File(basePath + "/mp3");
        if(!originalDir.exists()) {
            originalDir.mkdirs();
            System.out.println("mkdirs: original");
        }
        if(!mp3Dir.exists()) {
            mp3Dir.mkdirs();
            System.out.println("mkdirs: mp3");
        }

        List<Boolean> boolList = new ArrayList<>();
        for(MultipartFile file : files) {

            String originalName = file.getOriginalFilename();
            String filePath = basePath + "/original/" + originalName;
            String mp3Path = basePath + "/mp3/"
                    + file.getOriginalFilename().substring(0, originalName.lastIndexOf("."))
                    + ".mp3";

            File dest = new File(filePath);
            file.transferTo(dest);

            boolList.add(TimidityRunner.convertMidiToMp3(filePath, mp3Path));
        }

        System.out.println(requestDTO);
        return boolList;

    }

    @GetMapping("/upload")
    public ModelAndView upt() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upload-test");
        return modelAndView;
    }

    public static void main(String[] args) {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi/original";

    }

}
