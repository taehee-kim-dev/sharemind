package portfolio2.module.account.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.account.dto.request.*;
import portfolio2.module.account.service.AccountSettingService;
import portfolio2.module.account.validator.*;

import javax.validation.Valid;
import java.util.List;

import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.*;
import static portfolio2.module.main.config.StaticVariableNamesAboutMain.*;

@RequiredArgsConstructor
@Controller
public class AccountSettingController {

    private final ProfileUpdateRequestDtoValidator profileUpdateRequestDtoValidator;
    private final TagUpdateRequestDtoValidator tagUpdateRequestDtoValidator;
    private final PasswordUpdateRequestDtoValidator passwordUpdateRequestDtoValidator;
    private final AccountNicknameUpdateRequestDtoValidator accountNicknameUpdateRequestDtoValidator;
    private final AccountEmailUpdateRequestDtoValidator accountEmailUpdateRequestDtoValidator;

    private final AccountSettingService accountSettingService;

    private final ModelMapper modelMapper;

    @InitBinder("profileUpdateRequestDto")
    public void initBinderForProfileUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(profileUpdateRequestDtoValidator);
    }

    @InitBinder("tagUpdateRequestDto")
    public void initBinderForTagUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(tagUpdateRequestDtoValidator);
    }

    @InitBinder("passwordUpdateRequestDto")
    public void initBinderForPasswordUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(passwordUpdateRequestDtoValidator);
    }

    @InitBinder("accountNicknameUpdateRequestDto")
    public void initBinderForAccountNicknameUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountNicknameUpdateRequestDtoValidator);
    }

    @InitBinder("accountEmailUpdateRequestDto")
    public void initBinderForAccountEmailUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountEmailUpdateRequestDtoValidator);
    }

    // 프로필 설정

    @GetMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String showProfileUpdatePage(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute(modelMapper.map(sessionAccount, ProfileUpdateRequestDto.class));
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String updateProfile(@SessionAccount Account sessionAccount,
                                @Valid @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto,
                                Errors errors, Model model,
                                RedirectAttributes redirectAttributes){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);

        if(errors.hasErrors()){
            model.addAttribute(profileUpdateRequestDto);
            return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
        }

        accountSettingService.updateProfileAndSession(sessionAccount, profileUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "프로필 수정이 완료되었습니다.");
        return "redirect:" + ACCOUNT_SETTING_PROFILE_URL;
    }


    // 알림 설정

    @GetMapping(ACCOUNT_SETTING_NOTIFICATION_URL)
    public String showNotificationUpdateView(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute(modelMapper.map(sessionAccount, NotificationUpdateRequestDto.class));
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_NOTIFICATION_URL)
    public String notificationUpdate(@SessionAccount Account sessionAccount,
                                     @Valid @ModelAttribute NotificationUpdateRequestDto notificationUpdateRequestDto,
                                     Errors errors, Model model,
                                     RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute(notificationUpdateRequestDto);
            return ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME;
        }

        accountSettingService.updateNotificationAndSession(sessionAccount, notificationUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "알림설정이 저장되었습니다.");
        return REDIRECT + ACCOUNT_SETTING_NOTIFICATION_URL;
    }


    // 관심 태그 설정

    @GetMapping(ACCOUNT_SETTING_TAG_URL)
    public String showTagUpdateView(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        List<String> tag = accountSettingService.getInterestTagOfAccount(sessionAccount);
        model.addAttribute("tag", tag);
        return ACCOUNT_SETTING_TAG_VIEW_NAME;
    }

    @ResponseBody
    @PostMapping(ACCOUNT_SETTING_TAG_URL + "/add")
    public ResponseEntity addTag(@SessionAccount Account sessionAccount,
                                 @RequestBody @Valid TagUpdateRequestDto tagUpdateRequestDto, Errors errors){
        if (errors.hasFieldErrors("tagTitle")){
            return ResponseEntity.badRequest().build();
        }
        accountSettingService.addInterestTagToAccount(sessionAccount, tagUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping(ACCOUNT_SETTING_TAG_URL + "/remove")
    public ResponseEntity removeTag(@SessionAccount Account sessionAccount,
                                    @RequestBody @Valid TagUpdateRequestDto tagUpdateRequestDto, Errors errors){
        if (errors.hasFieldErrors("tagTitle")){
            return ResponseEntity.badRequest().build();
        }
        boolean result = accountSettingService.removeTagFromAccount(sessionAccount, tagUpdateRequestDto);

        if(!result){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    // 비밀번호 변경

    @GetMapping(ACCOUNT_SETTING_PASSWORD_URL)
    public String showPasswordUpdatePage(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute(new PasswordUpdateRequestDto());
        return ACCOUNT_SETTING_PASSWORD_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_PASSWORD_URL)
    public String postPasswordUpdate(@SessionAccount Account sessionAccount,
                                    @Valid @ModelAttribute PasswordUpdateRequestDto passwordUpdateRequestDto,
                                    Errors errors, Model model,
                                    RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute(passwordUpdateRequestDto);
            return ACCOUNT_SETTING_PASSWORD_VIEW_NAME;
        }

        accountSettingService.updatePasswordAndSession(sessionAccount, passwordUpdateRequestDto);

        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "비밀번호 변경이 완료되었습니다.");
        return REDIRECT + ACCOUNT_SETTING_PASSWORD_URL;
    }

    // 계정 정보 변경

    @GetMapping(ACCOUNT_SETTING_ACCOUNT_URL)
    public String showAccountSettingAccountView(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute(modelMapper.map(sessionAccount, AccountNicknameUpdateRequestDto.class));
        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
        if (sessionAccount.isEmailVerified()){
            accountEmailUpdateRequestDto.setEmail(sessionAccount.getVerifiedEmail());
        }else{
            accountEmailUpdateRequestDto.setEmail(sessionAccount.getEmailWaitingToBeVerified());
        }
        model.addAttribute(accountEmailUpdateRequestDto);
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
    }

    // 닉네임 변경

    @PostMapping(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
    public String updateAccountNickname(@SessionAccount Account sessionAccount,
                                         @Valid @ModelAttribute AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto,
                                         Errors errors, Model model,
                                         RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute(accountNicknameUpdateRequestDto);
            AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
            if (sessionAccount.isEmailVerified()){
                accountEmailUpdateRequestDto.setEmail(sessionAccount.getVerifiedEmail());
            }else{
                accountEmailUpdateRequestDto.setEmail(sessionAccount.getEmailWaitingToBeVerified());
            }
            model.addAttribute(accountEmailUpdateRequestDto);
            return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
        }

        accountSettingService.updateAccountNicknameAndSession(sessionAccount, accountNicknameUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "닉네임 변경이 완료되었습니다.");
        return REDIRECT + ACCOUNT_SETTING_ACCOUNT_URL;
    }

    // 이메일 변경
    @PostMapping(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
    public String updateAccountEmail(@SessionAccount Account sessionAccount,
                                        @Valid @ModelAttribute AccountEmailUpdateRequestDto accountEmailUpdateRequestDto,
                                        Errors errors, Model model,
                                        RedirectAttributes redirectAttributes){

        model.addAttribute(SESSION_ACCOUNT, sessionAccount);

        if(errors.hasErrors()){
            AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto = new AccountNicknameUpdateRequestDto();
            accountNicknameUpdateRequestDto.setNickname(sessionAccount.getNickname());
            model.addAttribute(accountNicknameUpdateRequestDto);
            model.addAttribute(accountEmailUpdateRequestDto);
            return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
        }

        if (!accountSettingService.canSendEmailVerificationEmail(sessionAccount)){
            model.addAttribute(ERROR_TITLE, "인증 이메일 전송 에러");
            model.addAttribute(ERROR_CONTENT, "이메일 인증 이메일은 12시간동안 5번까지만 보낼 수 있습니다.");
            return ERROR_VIEW_NAME;
        }

        accountSettingService.updateAccountEmailAndSession(sessionAccount, accountEmailUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "인증 이메일을 발송했습니다.");
        return REDIRECT + ACCOUNT_SETTING_ACCOUNT_URL;
    }
}
