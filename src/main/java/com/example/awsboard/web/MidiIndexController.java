package com.example.awsboard.web;

import com.example.awsboard.config.auth.LoginUser;
import com.example.awsboard.config.auth.dto.SessionUser;
import com.example.awsboard.service.posts.MidiService;
import com.example.awsboard.web.dto.midi.MidiPublicResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MidiIndexController {

    private final MidiService midiService;

    @GetMapping("/midi/upload")
    public ModelAndView upload(ModelAndView modelAndView) {
        modelAndView.setViewName("midi/upload");
        return modelAndView;
    }

    @GetMapping("midi/update")
    public String updateInfoPage(Model model, @LoginUser SessionUser user) {

        List<MidiPublicResponseDTO> list = null;
        System.out.println(user != null ? user :  "no user");

        if(user != null && user.getRole().equalsIgnoreCase("ADMIN")) {
            list = midiService.findAll();
        } else if(user != null) {
            list = midiService.findByUserId(user.getId());
        }

        System.out.println(">>>> " + list);

        model.addAttribute("list", list);
        return "midi/midi-edit";
    }

    @GetMapping("/midi")
    public String midiHome(Model model, @LoginUser SessionUser user) {
        if(user != null) {
            model.addAttribute("loginUser", user);
        }
        return "midi/midi-main";
    }


}
