package com.example.awsboard.web;

import com.example.awsboard.config.auth.LoginUser;
import com.example.awsboard.config.auth.dto.SessionUser;
import com.example.awsboard.service.posts.MidiService;
import com.example.awsboard.util.TimidityRunner;
import com.example.awsboard.web.dto.midi.MidiRequestDTO;
import com.example.awsboard.web.dto.midi.MidiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import java.util.*;

@RequiredArgsConstructor
@RestController
public class MidiApiController {

    private final MidiService midiService;
    private final String DEFAULT_URI = "/api/v1/midi";

    @PostMapping(DEFAULT_URI)
    public Map<String, Object> uploadMultipleMidi(@RequestParam("files") List<MultipartFile> files,
                                           @RequestParam("categories") List<String> categories,
                                           @RequestParam("titles") List<String> titles,
                                           @LoginUser SessionUser user) throws Exception {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi";

        Map<String, Object> result = new HashMap<>();

        if(files.size() == 0) {
            result.put("status", "Empty files");
            result.put("successList", null);
            result.put("failedList", null);

            return result;
        }

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

        List<Map<String, String>> successList = new ArrayList<>();
        List<String> urlList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();
        MultipartFile file = null;
        for(int i = 0; i < files.size(); i++) {

            file = files.get(i);

            String originalName = file.getOriginalFilename();
            String filePath = basePath + "/original/" + originalName;
            // String mp3Name = file.getOriginalFilename().substring(0, originalName.lastIndexOf("."));
            String mp3Name = UUID.randomUUID().toString();
            String mp3Path = basePath + "/mp3/" + mp3Name+ ".mp3";

            File dest = new File(filePath);
            file.transferTo(dest);

            // 변환
            Boolean isConverted = TimidityRunner.convertMidiToMp3(filePath, mp3Path);
            if(isConverted) {
                Long id = midiService.save(MidiRequestDTO.builder()
                        .category(categories.get(i))
                        .customTitle(titles.get(i))
                        .hash(TimidityRunner.getHash(filePath))
                        .originalMidiPath("/original/" + originalName)
                        .originalMp3Path("/mp3/" + mp3Name+ ".mp3")
                        .originalFileName(originalName)
                        .userId(user.getId())
                        .build());
                Map<String, String> urlPair = new HashMap<>();
                urlPair.put("originalName", originalName);
                urlPair.put("url", "http://localhost:8080/api/v1/midi/mp3/" + id);
                successList.add(urlPair);


            } else {
                failedList.add(originalName);
            }



        }


        result.put("status", successList.size() == 0 ? "All failed." : "Converted some files.");
        result.put("successList", successList);
        result.put("failedList", failedList);
        System.out.println(">>>>>> Auth user >>>>> " + user);
        return result;
    }


//    @PostMapping(DEFAULT_URI + "/t")
//    public Map<String, Object> multi(@RequestParam("files") List<MultipartFile> files,
//                                     @RequestParam("categories") List<String> categories,
//                                     @RequestParam("titles") List<String> titles,
//                                     Authentication user) {
//
//        Map<String, Object> result = new HashMap<>();
//
//        System.out.println(categories);
//        System.out.println(titles);
//        System.out.println(user);
//        System.out.println(user.getAuthorities());
//        System.out.println(user.getName());
//        for(MultipartFile file : files) {
//            System.out.println(file.getOriginalFilename());
//        }
//
//        return result;
//    }


    @GetMapping(DEFAULT_URI + "/mp3/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void mp3Play(ModelAndView modelAndView, @PathVariable Long id,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi";
        MidiResponseDTO mp3 = midiService.findById(id);
        String mp3Path = basePath + mp3.getOriginalMp3Path();

        File initFile = new File(mp3Path);

        String downloadName = id + "-" + mp3.getCustomTitle() + ".mp3";
        String browser = request.getHeader("User-Agent");

        //파일 인코딩
        if(browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")){
            //브라우저 확인 파일명 encode
            downloadName = URLEncoder.encode(downloadName, "UTF-8").replaceAll("\\+", "%20");
        }else{
            downloadName = new String(downloadName.getBytes("UTF-8"), "ISO-8859-1");
        }

        response.setHeader("Content-Disposition", "attachment;filename=\"" + downloadName +"\"");
        response.setContentType("audio/mp3");
        response.setHeader("Content-Transfer-Encoding", "binary;");

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

    @GetMapping("/midi/upload")
    public ModelAndView upt() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/midi/upload");
        return modelAndView;
    }

    public static void main(String[] args) {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi/original";


    }

}
