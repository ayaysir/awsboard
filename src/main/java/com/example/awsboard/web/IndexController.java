package com.example.awsboard.web;

import com.example.awsboard.config.auth.LoginUser;
import com.example.awsboard.config.auth.dto.SessionUser;
import com.example.awsboard.service.posts.LogService;
import com.example.awsboard.service.posts.NoticeService;
import com.example.awsboard.service.posts.PostsService;
import com.example.awsboard.util.Paginator;
import com.example.awsboard.web.dto.PostsListResponseDTO;
import com.example.awsboard.web.dto.PostsResponseDTO;
import com.example.awsboard.web.dto.log.LogSaveRequestDTO;
import com.example.awsboard.web.dto.notice.NoticeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final NoticeService noticeService;
    private final LogService logService;

    private static final Integer POSTS_PER_PAGE = 10;
    private static final Integer PAGES_PER_BLOCK = 5;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user,
                        @RequestParam(value = "page", defaultValue = "1") Integer page) {


        // 글 목록 전송
        model.addAttribute("boardTitle", "자유게시판");
        model.addAttribute("requestFrom", "posts");

        // 페이지네이션
        try {
            Paginator paginator = new Paginator(PAGES_PER_BLOCK, POSTS_PER_PAGE, postsService.count());
            Map<String, Object> pageInfo = paginator.getFixedBlock(page);

            model.addAttribute("pageInfo", pageInfo);
        } catch(IllegalStateException e) {
            model.addAttribute("pageInfo", null);
            System.err.println(e);
        }

        model.addAttribute("posts", postsService.findAllByOrderByIdDesc(page, POSTS_PER_PAGE));

        // 사용자 정보: 위의 @LoginUser 어노테이션으로 대체
        // SessionUser user = (SessionUser) httpSession.getAttribute("user");


        if(user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("isAllowWrite", true);
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
            model.addAttribute("requestFrom", "posts");

            return "posts-save";
        } else {
            return "redirect:/";
        }

    }

    @GetMapping("/posts/update/{id}")
    public String postUpdate(@PathVariable Long id, Model model, @LoginUser SessionUser loginUser) {

        PostsResponseDTO dto = postsService.findById(id);
        model.addAttribute("post", dto);
        model.addAttribute("requestFrom", "posts");

        if(!dto.getAuthorId().equals(loginUser.getId())) {
            return "redirect:/";
        }

        return "posts-update";
    }

    @GetMapping("/posts/view/{id}")
    public String postView(@PathVariable Long id, Model model, @LoginUser SessionUser loginUser) {

        PostsResponseDTO dto = postsService.findById(id);
        dto.setViewCount(logService.getViewCountByBoardNameAndArticleId("posts", dto.getId()));
        model.addAttribute("post", dto);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("requestFrom", "posts");

        Long userId = -99l;
        if(loginUser != null) {
            System.out.println(">> DTO: " + dto.getAuthorId());
            System.out.println(">> LoginUser: " + loginUser.getId());

            userId = loginUser.getId();
        }
        logService.save(LogSaveRequestDTO.builder().articleId(id).boardName("posts").userId(loginUser.getId()).build());

        return "posts-view";
    }

    /**
     * notice
     */

    @GetMapping("/notice")
    public String noticeIndex(Model model, @LoginUser SessionUser user) {
        // 글 목록 전송
        model.addAttribute("posts", noticeService.findAllDesc());
        model.addAttribute("boardTitle", "공지사항");
        model.addAttribute("requestFrom", "notice");

        // 사용자 정보: 위의 @LoginUser 어노테이션으로 대체
        // SessionUser user = (SessionUser) httpSession.getAttribute("user");


        if(user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("isAllowWrite", false);

            String userRole = user.getRole();
            System.out.println(">>>>>>>>>>>>" + userRole);

            if(user.getRole().equalsIgnoreCase("ADMIN")) {
                model.addAttribute("isAllowWrite", true);
            }
        }

        return "index";
    }

    @GetMapping("/notice/save")
    public String noticeSave(Model model, @LoginUser SessionUser user) {
        if(user != null && user.getRole().equalsIgnoreCase("ADMIN")) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("userId", user.getId());
            model.addAttribute("requestFrom", "notice");

            return "posts-save";
        } else {
            return "redirect:/";
        }

    }

    @GetMapping("/notice/update/{id}")
    public String noticeUpdate(@PathVariable Long id, Model model, @LoginUser SessionUser loginUser) {

        NoticeResponseDTO dto = noticeService.findById(id);
        model.addAttribute("post", dto);
        model.addAttribute("requestFrom", "notice");

        if(!dto.getAuthorId().equals(loginUser.getId())) {
            return "redirect:/";
        }

        return "posts-update";
    }

    @GetMapping("/notice/view/{id}")
    public String noticeView(@PathVariable Long id, Model model, @LoginUser SessionUser loginUser) {

        NoticeResponseDTO dto = noticeService.findById(id);
        dto.setViewCount(logService.getViewCountByBoardNameAndArticleId("notice", dto.getId()));
        model.addAttribute("post", dto);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("requestFrom", "notice");

        Long userId = -99l;
        if(loginUser != null) {
            System.out.println(">> DTO: " + dto.getAuthorId());
            System.out.println(">> LoginUser: " + loginUser.getId());

            userId = loginUser.getId();
        }
        logService.save(LogSaveRequestDTO.builder().articleId(id).boardName("notice").userId(loginUser.getId()).build());

        return "posts-view";
    }

    @GetMapping("/search")
    public String search(Model model, @LoginUser SessionUser user, String keyword) {

        List<PostsListResponseDTO> posts = null;
        if(!keyword.equals("")) {
            posts = postsService.searchTitleAndContent(keyword);
        } else {
            posts = postsService.findAllDesc();
        }
        // 글 목록 전송
        model.addAttribute("posts", posts);
        model.addAttribute("boardTitle", "자유게시판");
        model.addAttribute("requestFrom", "posts");

        posts.forEach(System.out::println);

        // 사용자 정보: 위의 @LoginUser 어노테이션으로 대체
        // SessionUser user = (SessionUser) httpSession.getAttribute("user");


        if(user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("isAllowWrite", true);
        }

        return "index";
    }

}
