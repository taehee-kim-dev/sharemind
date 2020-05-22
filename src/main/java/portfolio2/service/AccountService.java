package portfolio2.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.tag.Tag;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.UserAccount;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.dto.account.profileupdate.AccountNicknameUpdateRequestDto;
import portfolio2.dto.account.profileupdate.NotificationUpdateRequestDto;
import portfolio2.dto.account.profileupdate.PasswordUpdateRequestDto;
import portfolio2.dto.account.profileupdate.ProfileUpdateRequestDto;
import portfolio2.mail.EmailMessage;
import portfolio2.mail.EmailService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    public void processNewAccount(SignUpRequestDto signUpRequestDto) {
        Account newAccount = saveNewAccount(signUpRequestDto);
        sendSignUpConfirmEmail(newAccount);
        this.loginOrUpdateSessionAccount(newAccount);
    }

    public Account saveNewAccount(@Valid SignUpRequestDto signUpRequestDto) {
        signUpRequestDto.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        Account account = modelMapper.map(signUpRequestDto, Account.class);

        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        newAccount.generateEmailCheckToken();

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("ShareMind 회원가입 이메일 인증")
                .message("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                        "&email=" + newAccount.getEmail())
                .build();
        emailService.sendEmail(emailMessage);
    }

    public void resendSignUpConfirmEmail(Account sessionAccount) {

        Account accountInDb = accountRepository.findByUserId(sessionAccount.getUserId());
        accountInDb.generateEmailCheckToken();

        EmailMessage emailMessage = EmailMessage.builder()
                .to(accountInDb.getEmail())
                .subject("ShareMind 회원가입 이메일 인증")
                .message("/check-email-token?token=" + accountInDb.getEmailCheckToken() +
                        "&email=" + accountInDb.getEmail())
                .build();
        emailService.sendEmail(emailMessage);
    }


    public void loginOrUpdateSessionAccount(Account account) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String userIdOrEmail) throws UsernameNotFoundException {

        Account account = accountRepository.findByUserId(userIdOrEmail);

        if (account == null) {
            account = accountRepository.findByEmail(userIdOrEmail);
        }

        if (account == null) {
            throw new UsernameNotFoundException(userIdOrEmail);
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        Account accountInDb = accountRepository.findByUserId(account.getUserId());
        accountInDb.completeSignUp();
        loginOrUpdateSessionAccount(accountInDb);
    }

    public void updateProfile(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        modelMapper.map(profileUpdateRequestDto, sessionAccount);
        accountRepository.save(sessionAccount);
        loginOrUpdateSessionAccount(sessionAccount);
    }

    public void updatePassword(Account sessionAccount, PasswordUpdateRequestDto passwordUpdateRequestDto) {
        sessionAccount.setPassword(passwordEncoder.encode(passwordUpdateRequestDto.getNewPassword()));
        accountRepository.save(sessionAccount);
        loginOrUpdateSessionAccount(sessionAccount);
        this.sendPasswordChangeNotificationEmail(sessionAccount);
    }

    public void sendPasswordChangeNotificationEmail(Account sessionAccount) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(sessionAccount.getEmail())
                .subject("ShareMind 비밀번호 변경 알림")
                .message(sessionAccount.getUserId() + "의 비밀번호가 변경되었습니다.")
                .build();
        emailService.sendEmail(emailMessage);
    }

    public void updateNotification(Account sessionAccount, NotificationUpdateRequestDto notificationUpdateRequestDto) {
        modelMapper.map(notificationUpdateRequestDto, sessionAccount);
        accountRepository.save(sessionAccount);
        loginOrUpdateSessionAccount(sessionAccount);
    }

    public void updateAccountNickname(Account sessionAccount, AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto) {
        modelMapper.map(accountNicknameUpdateRequestDto, sessionAccount);
        accountRepository.save(sessionAccount);
        loginOrUpdateSessionAccount(sessionAccount);
    }

    public void sendLoginEmail(Account account) {
        Account accountInDb = accountRepository.findByUserId(account.getUserId());
        accountInDb.generateLoginEmailToken();

        EmailMessage emailMessage = EmailMessage.builder()
                .to(accountInDb.getEmail())
                .subject("ShareMind 로그인 링크")
                .message("/check-email-token?token=" + accountInDb.getEmailCheckToken() +
                        "&email=" + accountInDb.getEmail())
                .build();
        emailService.sendEmail(emailMessage);
    }

    public List<String> getTag(Account sessionAccount) {
        Account existingAccount = accountRepository.findByUserId(sessionAccount.getUserId());
        Set<Tag> tagOfExistingAccount = existingAccount.getTag();
        return tagOfExistingAccount.stream().map(Tag::getTitle).collect(Collectors.toList());
    }

    public void addTag(Account sessionAccount, Tag newTag) {
        Account existingAccount = accountRepository.findByUserId(sessionAccount.getUserId());
        if(existingAccount != null){
            existingAccount.getTag().add(newTag);
        }
    }

    public void removeTag(Account sessionAccount, Tag tagToRemove) {
        Account existingAccount = accountRepository.findByUserId(sessionAccount.getUserId());
        if(existingAccount != null){
            existingAccount.getTag().remove(tagToRemove);
        }
    }
}
