package com.example.awsboard.web;

import com.example.awsboard.config.auth.LoginUser;
import com.example.awsboard.config.auth.dto.SessionUser;
import com.example.awsboard.service.posts.MidiService;
import com.example.awsboard.util.TimidityRunner;
import com.example.awsboard.web.dto.midi.MidiPublicResponseDTO;
import com.example.awsboard.web.dto.midi.MidiRequestDTO;
import com.example.awsboard.web.dto.midi.MidiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
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
                                           @LoginUser SessionUser user, HttpServletRequest request) throws Exception {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi";
        String ourUrl = request.getRequestURL().toString().replace(request.getRequestURI(),"");

        Map<String, Object> result = new HashMap<>();

        if(files.size() == 0) {
            result.put("status", "NoFile");
            result.put("successList", null);
            result.put("failedList", null);

            return result;
        }

        if(!user.getRole().equalsIgnoreCase("ADMIN") && files.size() > 5) {
            result.put("status", "NotAllowManyFile");
            result.put("successList", null);
            result.put("failedList", null);

            return result;
        }

        System.out.println(">>> " + rootPath);
        System.out.println(">>> " + basePath);

        File originalDir = new File(basePath + "/original");
        File mp3Dir = new File(basePath + "/mp3");

        // 디렉토리가 없으면 만든다.
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
            String originalExt = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            String uuid = UUID.randomUUID().toString();
            String filePath = basePath + "/original/" + uuid + originalExt;
            String mp3Path = basePath + "/mp3/" + uuid + ".mp3";

            File dest = new File(filePath);
            file.transferTo(dest);

            // 변환
            Boolean isConverted = false;
            try {
                isConverted = TimidityRunner.convertMidiToMp3(filePath, mp3Path);
            } catch(IOException e) {
                System.err.println(e);
                isConverted = false;
            }

            // 변환 성공시 데이터베이스에 정보 입력
            if(isConverted) {
                Long id = midiService.save(MidiRequestDTO.builder()
                        .category(categories.get(i))
                        .customTitle(titles.get(i))
                        .hash(TimidityRunner.getHash(filePath))
                        .originalMidiPath("/original/" + uuid + originalExt)
                        .originalMp3Path("/mp3/" + uuid + ".mp3")
                        .originalFileName(originalName)
                        .userId(user.getId())
                        .build());
                Map<String, String> urlPair = new HashMap<>();
                urlPair.put("originalName", originalName);
                urlPair.put("url", ourUrl + "/api/v1/midi/mp3/" + id);
                successList.add(urlPair);


            } else {
                failedList.add(originalName);
            }
        }

        if(successList.size() > 0 && failedList.size() == 0) {
            result.put("status", "AllFileSuccess");
        } else if(successList.size() > 0 && failedList.size() > 0) {
            result.put("status", "SomeFileSuccess");
        } else if (successList.size() > 0){
            result.put("status", "AllFileFailed");
        }
        result.put("successList", successList);
        result.put("failedList", failedList);
        System.out.println(">>>>>> Auth user >>>>> " + user);
        return result;
    }

    @GetMapping(DEFAULT_URI + "/mp3/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void mp3Play(ModelAndView modelAndView, @PathVariable Long id,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {

        // mp3 파일 경로 지정
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi";
        MidiResponseDTO mp3 = midiService.findById(id);
        String mp3Path = basePath + mp3.getOriginalMp3Path();

        // File 객체 생성
        File initFile = new File(mp3Path);

        // ***** RANGE 추출 ***** //
        Long startRange = 0l;
        Long endRange = 0l;
        Boolean isPartialRequest = false;

        try {
            if(request.getHeader("range") != null) {
                String rangeStr = request.getHeader("range");

                System.out.println(rangeStr);

                String[] range = rangeStr
                        .replace("bytes=", "").split("-");

                startRange = range[0] != null ? Long.parseLong(range[0]) : 0l;
                if(range[1] != null) {
                    endRange = Long.parseLong(range[1]);
                    isPartialRequest = true;
                }

                System.out.println(">>>>> range >>>>> " + range[0] + " : " + range[1]);
            }
        } catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
            System.err.println(e);
        }
        // ****************** //

        // 파일 다운로드 이름 생성
        String downloadName = id + "-" + mp3.getCustomTitle() + ".mp3";
        String browser = request.getHeader("User-Agent");

        // 파일 인코딩
        if(browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")){
            // 브라우저 확인 파일명 encode
            downloadName = URLEncoder.encode(downloadName, "UTF-8").replaceAll("\\+", "%20");
        } else {
            downloadName = new String(downloadName.getBytes("UTF-8"), "ISO-8859-1");
        }

        // 리스폰스 헤더 설정
        response.setHeader("Content-Disposition", "filename=\"" + downloadName +"\"");
        response.setContentType("audio/mpeg");
        response.setHeader("Accept-Ranges", "bytes"); // 크롬 구간문제 해결용
        response.setHeader("Content-Transfer-Encoding", "binary;");


        // 부분 범위 리퀘스트인지, 전체 범위 리퀘스트인지에 따라 Content-Range 값을 다르게
        if(isPartialRequest) {
            response.setHeader("Content-Range", "bytes " + startRange + "-"
                    + endRange + "/" + initFile.length());
        } else {
            response.setHeader("Content-Length", initFile.length() + "");
            response.setHeader("Content-Range", "bytes 0-"
                    + initFile.length() + "/" + initFile.length());
            startRange = 0l;
            endRange = initFile.length();
        }

        // 랜덤 액세스 파일을 이용해 mp3 파일을 범위로 읽기
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(initFile, "r");
            ServletOutputStream sos = response.getOutputStream();	){

            Integer bufferSize = 1024, data = 0;
            byte[] b = new byte[bufferSize];
            Long count = startRange;
            Long requestSize = endRange - startRange + 1;

            // startRange에서 출발
            randomAccessFile.seek(startRange);

            while(true) {
                // 버퍼 사이즈 (1024) 보다 범위가 작으면
                if(requestSize <= 2) {
                    // Range byte 0-1은 아래 의미가 아님.
                    // data = randomAccessFile.read(b, 0, requestSize.intValue());
                    // sos.write(b, 0, data);

                    // ** write 없이 바로 flush ** //
                    sos.flush();
                    break;
                }

                // 나머지는 일반적으로 진행
                data = randomAccessFile.read(b, 0, b.length);

                // count가 endRange 이상이면 요청 범위를 넘어선 것이므로 종료
                if(count <= endRange) {
                    sos.write(b, 0, data);
                    count += bufferSize;
                    randomAccessFile.seek(count);
                } else {
                    break;
                }

            }

            sos.flush();
        }

    }

    @GetMapping(DEFAULT_URI + "/mp3-adv/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Resource> mp3PlayAdv(ModelAndView modelAndView,
                                               @PathVariable Long id,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {

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

        InputStreamResource resource = new InputStreamResource(new FileInputStream(initFile));


        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Disposition", "filename=\"" + downloadName +"\"");
        headers.set("Accept-Ranges", "bytes");
        headers.set("Content-Length", initFile.length() + "");
        headers.set("Content-Transfer-Encoding", "binary;");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(initFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping(DEFAULT_URI)
    public List<MidiPublicResponseDTO> findAll() {
        return midiService.findAll();
    }

    @PutMapping(DEFAULT_URI + "/{id}")
    public Long updateMidiInfo(@RequestBody MidiRequestDTO midiRequestDTO,
                                              @PathVariable Long id, @LoginUser SessionUser user) {
        if(user != null) {
            MidiResponseDTO midi = midiService.findById(id);
            System.out.println(">>>> " + midiRequestDTO);
            System.out.println(">>>> " + midi);
            System.out.println(user.getRole());
            System.out.println(user.getId());
            System.out.println(midi.getUserId());
            System.out.println(midi.getUserId().equals(user.getId()));
            if(user.getRole().equalsIgnoreCase("ADMIN")
                    || midi.getUserId().equals(user.getId())) {
                return midiService.update(id, midiRequestDTO);
            }

        }

        return -99L;
    }

    @DeleteMapping(DEFAULT_URI + "/{id}")
    public Long deleteMidi(@PathVariable Long id,
                           @LoginUser SessionUser user) {

        if(user != null) {
            MidiResponseDTO midi = midiService.findById(id);
            if(user.getRole().equalsIgnoreCase("ADMIN")
                    ||  midi.getUserId().equals(user.getId())) {
                midiService.delete(id);

                // 파일 삭제
                // mp3 파일 경로 지정
                String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
                String basePath = rootPath + "/" + "app/midi";
                String midiPath = basePath + midi.getOriginalMidiPath();
                String mp3Path = basePath + midi.getOriginalMp3Path();

                File midiFile = new File(midiPath);
                File mp3File = new File(mp3Path);
                File toDeleteDir = new File(basePath + "/to_delete");

                if(!toDeleteDir.exists()) {
                    toDeleteDir.mkdirs();
                    System.out.println("mkdirs: /to_delete");
                }

                // 파일 to_delete 디렉토리로 이동
                midiFile.renameTo(new File(basePath + "/to_delete/" + midiFile.getName()));
                mp3File.renameTo(new File(basePath + "/to_delete/" + mp3File.getName()));

                return id;
            }

        }

        return -99L;
    }

    public static void main(String[] args) {
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + "app/midi/mp3";

        String name = "/0ae62b33-d1c2-4969-820b-692ce027277c.mp3";
        String ext = name.substring(name.lastIndexOf("."));
        System.out.println(ext);

        File file = new File(basePath + name);

        try(FileInputStream fis = new FileInputStream(file);){

            byte[] b = new byte[1024];
            int data = 0;
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            System.out.println("file size byte: " + file.length());
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(102654);
            int start = 102614, end = 673225, count = 0;

            while(true) {
                data = randomAccessFile.read(b, 0, b.length);
                if(start <= end) {
                    System.out.println(data);
                    start += 1024;
                    count += 1;
                    randomAccessFile.seek(start);
                } else {
                    System.out.println(count);
                    System.out.println(end);
                    break;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
