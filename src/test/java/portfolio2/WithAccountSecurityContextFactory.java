package portfolio2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import portfolio2.service.AccountService;
import portfolio2.web.dto.SignUpRequestDto;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {

        String userId = withAccount.value();

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(userId)
                .nickname("testNickname")
                .email("test@email.com")
                .password("12345678")
                .build();

        accountService.processNewAccount(signUpRequestDto);

        // Authentication 만들고 SecurityContext에 넣어주기
        UserDetails principal = accountService.loadUserByUsername(userId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}