package portfolio2.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import portfolio2.domain.account.config.CustomPrincipal;
import portfolio2.service.account.AccountService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class LogInAndOutProcessForTest {

    private final AccountService accountService;

    public void logIn(String userId){
        // Authentication 만들고 SecurityContext에 넣어주기
        UserDetails customPrincipal = accountService.loadUserByUsername(userId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customPrincipal, customPrincipal.getPassword(), customPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void logOut(){
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public boolean isSomeoneLoggedIn(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null;
    }

    public boolean isLoggedInByUserId(String userId){
        CustomPrincipal customPrincipal
                = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userId.equals(customPrincipal.getSessionAccount().getUserId());
    }
}
