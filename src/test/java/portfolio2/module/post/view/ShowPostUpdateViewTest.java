package portfolio2.module.post.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.model.IModel;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.*;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.ERROR_VIEW_NAME;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_UPDATE_FORM_VIEW_NAME;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_UPDATE_URL;

/**
 * - 게시글 수정 화면 보여주기
 * 정상 보여주기
 * 존재하지 않는 게시물 에러
 * 글 수정 권한 없는 사용자 요청 에러
 *
 */

@MockMvcTest
public class ShowPostUpdateViewTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private PostService postService;

    private final String POST_TEST_TITLE = "Test title.";
    private final String POST_TEST_CONTENT = "Test content.";
    private final List<String> POST_TEST_TAG_STRING_LIST = List.of("test tagTitle 1", "test tagTitle 2", "test tagTitle 3");
    private Long savedPostId;

    @BeforeEach
    void beforeEach(){
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
        PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
        postNewPostRequestDto.setTitle(POST_TEST_TITLE);
        postNewPostRequestDto.setContent(POST_TEST_CONTENT);
        postNewPostRequestDto.setTagTitleOnPost(String.join(",", POST_TEST_TAG_STRING_LIST));
        CustomPrincipal customPrincipal = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account sessionAccount = customPrincipal.getSessionAccount();
        Post savedPost = postService.saveNewPostWithTag(sessionAccount, postNewPostRequestDto);
        this.savedPostId = savedPost.getId();
    }

    @AfterEach
    void afterEach(){
        tagRepository.deleteAll();
        postRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("게시글 수정 화면 보여주기 - 정상")
    @Test
    void showPostUpdatePage() throws Exception{
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
        mockMvc.perform(get(POST_UPDATE_URL + '/' + savedPostId))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("errorTitle"))
                .andExpect(model().attributeDoesNotExist("errorContent"))
                .andExpect(model().attributeExists("postUpdateRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(POST_UPDATE_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("게시글 수정 화면 보여주기 - 에러 - 존재하지 않는 게시물")
    @Test
    void notLoggedInError() throws Exception{
        mockMvc.perform(get(POST_UPDATE_URL + '/' + (savedPostId + 1)))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute("errorTitle", "게시물 조회 에러"))
                .andExpect(model().attributeExists("errorContent"))
                .andExpect(model().attributeDoesNotExist("postUpdateRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("게시글 수정 화면 보여주기 - 에러 - 권한 없는 사용자")
    @Test
    void notAuthorError() throws Exception{
        logInAndOutProcessForTest.logOut();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));
        mockMvc.perform(get(POST_UPDATE_URL + '/' + savedPostId))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute("errorTitle", "글 수정 권한 없음"))
                .andExpect(model().attributeExists("errorContent"))
                .andExpect(model().attributeDoesNotExist("postUpdateRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));
    }

}
