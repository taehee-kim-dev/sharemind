package portfolio2.module.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.SignUpRequestDto;
import portfolio2.module.account.service.SignUpService;

import static portfolio2.module.account.config.TestAccountInfo.*;

@Component
@RequiredArgsConstructor
public class SignUpAndLogOutEmailNotVerifiedProcessForTest {

    private final SignUpService signUpService;
    private final AccountRepository accountRepository;

    public Account signUpAndLogOutDefault(){
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TEST_USER_ID)
                .nickname(TEST_NICKNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        signUpService.signUp(signUpRequestDto);

        SecurityContextHolder.getContext().setAuthentication(null);
        return accountRepository.findByUserId(TEST_USER_ID);
    }

    public Account signUpAndLogOutNotDefaultWith(String testUserId){

        String userId = null;
        String nickname = null;
        String email = null;
        String password = null;

        switch(testUserId){
            case TEST_USER_ID_1:
                userId = TEST_USER_ID_1;
                nickname = TEST_NICKNAME_1;
                email = TEST_EMAIL_1;
                password = TEST_PASSWORD_1;
                break;
            case TEST_USER_ID_2:
                userId = TEST_USER_ID_2;
                nickname = TEST_NICKNAME_2;
                email = TEST_EMAIL_2;
                password = TEST_PASSWORD_2;
                break;
            default:
                throw new IllegalArgumentException("SignUpAndLogInProcess의 signUpAndLogIn의 매개변수 잘못넘김");
        }

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(userId)
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();

        signUpService.signUp(signUpRequestDto);
        SecurityContextHolder.getContext().setAuthentication(null);
        return accountRepository.findByUserId(userId);
    }
}
