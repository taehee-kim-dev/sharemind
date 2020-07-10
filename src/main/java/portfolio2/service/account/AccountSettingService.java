package portfolio2.service.account;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.domain.account.setting.NotificationUpdateProcess;
import portfolio2.domain.account.setting.PasswordUpdateProcess;
import portfolio2.domain.account.setting.ProfileUpdateProcess;
import portfolio2.domain.account.setting.TagUpdateProcess;
import portfolio2.domain.email.SendingPasswordUpdateNotificationEmailProcess;
import portfolio2.dto.account.TagUpdateRequestDto;
import portfolio2.dto.request.account.setting.NotificationUpdateRequestDto;
import portfolio2.dto.request.account.setting.PasswordUpdateRequestDto;
import portfolio2.dto.request.account.setting.ProfileUpdateRequestDto;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountSettingService {

    private final ProfileUpdateProcess profileUpdateProcess;
    private final NotificationUpdateProcess notificationUpdateProcess;
    private final TagUpdateProcess tagUpdateProcess;
    private final PasswordUpdateProcess passwordUpdateProcess;

    private final SendingPasswordUpdateNotificationEmailProcess sendingPasswordUpdateNotificationEmailProcess;


    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public void updateProfileAndSession(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        // 프로필 업데이트
        Account updatedAccount
                = profileUpdateProcess.updateProfile(sessionAccount, profileUpdateRequestDto);
        // 세션 업데이트
        logInOrSessionUpdateProcess.loginOrSessionUpdate(updatedAccount);
    }

    public void updateNotificationAndSession(Account sessionAccount, NotificationUpdateRequestDto notificationUpdateRequestDto) {
        Account updatedAccount
                = notificationUpdateProcess.updateNotification(sessionAccount, notificationUpdateRequestDto);

        logInOrSessionUpdateProcess.loginOrSessionUpdate(updatedAccount);
    }
    // TODO: 인증된 이메일 없으면 이메일 알림 받을 수 없음.

    public List<String> getInterestTagOfAccount(Account sessionAccount) {
        return tagUpdateProcess.getInterestTagOfAccount(sessionAccount);
    }

    public void addInterestTagToAccount(Account sessionAccount, TagUpdateRequestDto tagUpdateRequestDto) {
        tagUpdateProcess.addInterestTagToAccountIfNotHas(sessionAccount, tagUpdateRequestDto);
    }

    public boolean removeTagFromAccount(Account sessionAccount, TagUpdateRequestDto tagUpdateRequestDto) {
        return tagUpdateProcess.removeInterestTagFromAccount(sessionAccount, tagUpdateRequestDto);
    }

    public void updatePasswordAndSession(Account sessionAccount, PasswordUpdateRequestDto passwordUpdateRequestDto) {
        Account updatedAccount = passwordUpdateProcess.updatePassword(sessionAccount, passwordUpdateRequestDto);
        // 인증된 이메일이 있다면, 비밀번호 변경 알림 이메일 발송
        if (updatedAccount.isEmailVerified()){
            updatedAccount
                    = sendingPasswordUpdateNotificationEmailProcess.sendPasswordUpdateNotificationEmail(updatedAccount);
        }
        logInOrSessionUpdateProcess.loginOrSessionUpdate(updatedAccount);
    }
}
