package portfolio2.domain.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.EmailSendingProcess;
import portfolio2.dto.account.SignUpRequestDto;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Component
public class SignUpProcess {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSendingProcess emailSendingProcess;

    private Account newAccount;

    public void createNewAccount() {
        this.newAccount = new Account();
    }

    public void setInitialInformOfNewAccount(SignUpRequestDto signUpRequestDto) {
        signUpRequestDto.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        this.newAccount.setUserId(signUpRequestDto.getUserId());
        this.newAccount.setNickname(signUpRequestDto.getNickname());
        this.newAccount.setPassword(signUpRequestDto.getPassword());
        this.newAccount.setEmailWaitingToBeVerified(signUpRequestDto.getEmail());
        this.newAccount.setJoinedAt(LocalDateTime.now());
        accountRepository.save(this.newAccount);
    }

    public Account sendEmailVerificationEmail() {
        this.newAccount.generateEmailCheckToken();
        emailSendingProcess.sendEmailVerificationEmail(this.newAccount);
        return this.newAccount;
    }

    public void clearField(){
        this.newAccount = null;
    }
}
