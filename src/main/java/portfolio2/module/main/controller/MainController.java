package portfolio2.module.main.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.main.service.MainService;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.post.Post;

import java.util.List;

import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.REDIRECT;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final MainService mainService;

    @GetMapping(HOME_URL)
    public String home(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        return HOME_VIEW_NAME;
    }

    // 로그인 되어있으면 안됨.
    @GetMapping(LOGIN_URL)
    public String login(@SessionAccount Account sessionAccount){
        if(sessionAccount != null){
            return REDIRECT + HOME_URL;
        }
        return LOGIN_VIEW_NAME;
    }

    @GetMapping("/search/post")
    public String searchPost(@SessionAccount Account sessionAccount, String keyword,
                             @PageableDefault(size = 15, page = 0, sort = "firstWrittenDateTime", direction = Sort.Direction.DESC)
                                     Pageable pageable, Model model){
        Page<Post> postPage = mainService.findPostByKeyword(keyword, pageable);
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("keyword", keyword);
        model.addAttribute("postPage", postPage);
        return "search";
    }
}

