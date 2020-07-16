package com.example.awsboard.web;

import com.example.awsboard.config.auth.LoginUser;
import com.example.awsboard.config.auth.dto.SessionUser;
import com.example.awsboard.service.posts.PostsService;
import com.example.awsboard.web.dto.PostsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        // 글 목록 전송
        model.addAttribute("posts", postsService.findAllDesc());

        // 사용자 정보: 위의 @LoginUser 어노테이션으로 대체
        // SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if(user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
            model.addAttribute("userEmail", user.getEmail());
        }

        return "index";
    }

    @GetMapping("/posts/save")
    public String postSave(Model model, @LoginUser SessionUser user) {
        if(user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("userId", user.getId());

            return "posts-save";
        } else {
            return "redirect:/";
        }

    }

    @GetMapping("/posts/update/{id}")
    public String postUpdate(@PathVariable Long id, Model model, @LoginUser SessionUser loginUser) {

        PostsResponseDTO dto = postsService.findById(id);
        model.addAttribute("post", dto);

        if(!dto.getAuthorId().equals(loginUser.getId())) {
            return "redirect:/";
        }

        return "posts-update";
    }

    @GetMapping("/posts/view/{id}")
    public String postView(@PathVariable Long id, Model model, @LoginUser SessionUser loginUser) {

        PostsResponseDTO dto = postsService.findById(id);
        model.addAttribute("post", dto);
        model.addAttribute("loginUser", loginUser);

        System.out.println(">> DTO: " + dto.getAuthorId());
        System.out.println(">> LoginUser: " + loginUser.getId());

        return "posts-view";
    }

}
