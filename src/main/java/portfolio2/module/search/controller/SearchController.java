package portfolio2.module.search.controller;


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
import portfolio2.module.main.functions.CommonFunctions;
import portfolio2.module.post.Post;
import portfolio2.module.search.service.SearchService;

import static portfolio2.module.main.config.StaticVariableNamesAboutMain.SESSION_ACCOUNT;
import static portfolio2.module.search.controller.config.StaticVariableNamesAboutSearch.SEARCH_POST_RESULT_VIEW_NAME;
import static portfolio2.module.search.controller.config.StaticVariableNamesAboutSearch.SEARCH_POST_URL;

@RequiredArgsConstructor
@Controller
public class SearchController {

    private final SearchService searchService;
    private final CommonFunctions commonFunctions;

    @GetMapping(SEARCH_POST_URL)
    public String searchPost(@SessionAccount Account sessionAccount, String keyword,
                             @PageableDefault(size = 15, page = 0, sort = "firstWrittenDateTime", direction = Sort.Direction.DESC)
                                     Pageable pageable, Model model){
        keyword = keyword.trim();
        Page<Post> postPage = searchService.findPostByKeyword(keyword, pageable);
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("keyword", keyword);
        commonFunctions.addPagingAttributes(model, postPage, "postPage", "firstWrittenDateTime");
        return SEARCH_POST_RESULT_VIEW_NAME;
    }
}

