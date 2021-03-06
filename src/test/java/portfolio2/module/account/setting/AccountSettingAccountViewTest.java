package portfolio2.module.account.setting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerified;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.ACCOUNT_SETTING_ACCOUNT_URL;
import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
import static portfolio2.module.main.config.StaticVariableNamesAboutMain.SESSION_ACCOUNT;

@MockMvcTest
public class AccountSettingAccountViewTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 모두 무조건 로그인 상태여야 함

    @DisplayName("계정 설정 화면 보여주기")
    @SignUpAndLogInEmailNotVerified
    @Test
    void showAccounSettingAccountPage() throws Exception{
        mockMvc.perform(get(ACCOUNT_SETTING_ACCOUNT_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeExists("accountEmailUpdateRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }
}
