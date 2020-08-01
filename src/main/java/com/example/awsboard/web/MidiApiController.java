package com.example.awsboard.web;

import com.example.awsboard.service.posts.MidiService;
import com.example.awsboard.util.TimidityRunner;
import com.example.awsboard.web.dto.midi.MidiRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
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

    @PostMapping(DEFAULT_URI + "/test2")
    public ModelAndView midiPrototype(@RequestParam("files") List<MultipartFile> files,
                                      ModelAndView modelAndView, MidiRequestDTO requestDTO) throws Exception {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi";

        System.out.println(">>> " + rootPath);
        System.out.println(">>> " + basePath);

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

        List<String> mp3List = new ArrayList<>();
        for(MultipartFile file : files) {

            String originalName = file.getOriginalFilename();
            String filePath = basePath + "/original/" + originalName;
            String mp3Name = file.getOriginalFilename().substring(0, originalName.lastIndexOf("."));
            String mp3Path = basePath + "/mp3/" + mp3Name+ ".mp3";

            File dest = new File(filePath);
            file.transferTo(dest);

            TimidityRunner.convertMidiToMp3(filePath, mp3Path);
            mp3List.add(mp3Name);

        }

        System.out.println(requestDTO);
        modelAndView.addObject("mp3List", mp3List);
        modelAndView.setViewName("midi-mp3-test");
        return modelAndView;
    }


    @GetMapping(DEFAULT_URI + "/mp3/{mp3Name}")
    @ResponseStatus(HttpStatus.OK)
    public void mp3Play(ModelAndView modelAndView, @PathVariable String mp3Name,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi";

        File initFile = new File(basePath + "/mp3/" + mp3Name + ".mp3");

        String downloadName = mp3Name + ".mp3";
        String browser = request.getHeader("User-Agent");

        //파일 인코딩
        if(browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")){
            //브라우저 확인 파일명 encode
            downloadName = URLEncoder.encode(downloadName, "UTF-8").replaceAll("\\+", "%20");
        }else{
            downloadName = new String(downloadName.getBytes("UTF-8"), "ISO-8859-1");
        }

        try(FileInputStream fis = new FileInputStream(initFile);
            ServletOutputStream sos = response.getOutputStream();	){

            byte[] b = new byte[1024];
            int data = 0;

            while((data=(fis.read(b, 0, b.length))) != -1){
                sos.write(b, 0, data);
            }

            sos.flush();
        }

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
