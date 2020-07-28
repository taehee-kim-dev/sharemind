package portfolio2.module.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.ViewMenuType;
import portfolio2.module.notification.dto.NotificationDeleteRequestDto;
import portfolio2.module.notification.service.NotificationService;
import portfolio2.module.notification.validator.NotificationDeleteRequestDtoValidator;

import javax.validation.Valid;

import java.util.List;

import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.REDIRECT;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.notification.config.UrlAndViewNameAboutNotification.*;

@RequiredArgsConstructor
@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationDeleteRequestDtoValidator notificationDeleteRequestDtoValidator;

    @InitBinder("notificationDeleteRequestDto")
    public void initBinderForNotificationDeleteRequestDtoValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(notificationDeleteRequestDtoValidator);
    }

    @GetMapping(ALL_NOTIFICATION_LIST_URL)
    public String showALLNotificationList(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        List<Notification> allNotification = notificationService.ringBellCheck(sessionAccount);
        model.addAttribute("allNotification", allNotification);
        return ALL_NOTIFICATION_LIST_VIEW_NAME;
    }

    @GetMapping(LINK_UNVISITED_NOTIFICATION_LIST_URL)
    public String showLinkUnvisitedNotificationList(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        List<Notification> linkUnvisitedNotification = notificationService.getLinkUnvisitedNotification(sessionAccount);
        model.addAttribute("linkUnvisitedNotification", linkUnvisitedNotification);
        return LINK_UNVISITED_NOTIFICATION_LIST_VIEW_NAME;
    }

    @GetMapping(LINK_VISITED_NOTIFICATION_LIST_URL)
    public String showLinkVisitedNotificationList(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        List<Notification> linkVisitedNotification = notificationService.getLinkVisitedNotification(sessionAccount);
        model.addAttribute("linkVisitedNotification", linkVisitedNotification);
        return LINK_VISITED_NOTIFICATION_LIST_VIEW_NAME;
    }

    @GetMapping(NOTIFICATION_LINK_VISIT_URL + "/{notificationId}")
    public String visitLink(@SessionAccount Account sessionAccount,
                            @PathVariable("notificationId") Notification notification, Model model){
        notificationService.linkVisitCheck(notification);
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        return REDIRECT + notification.getLink();
    }

    @PostMapping(CHANGE_ALL_LINK_UNVISITED_NOTIFICATION_TO_VISITED_URL)
    public String changeAllToLinkVisited(@SessionAccount Account sessionAccount,
                                         @RequestParam ViewMenuType viewMenuType){
        notificationService.changeAllToLinkVisited(sessionAccount);
        return getRedirectUrl(viewMenuType);
    }

    @PostMapping(DELETE_ALL_LINK_VISITED_NOTIFICATION_URL)
    public String deleteAllLinkVisited(@SessionAccount Account sessionAccount,
                                       @RequestParam ViewMenuType viewMenuType){
        notificationService.deleteAllLinkVisited(sessionAccount);
        return getRedirectUrl(viewMenuType);
    }

    private String getRedirectUrl(@RequestParam ViewMenuType viewMenuType) {
        switch (viewMenuType) {
            case LINK_UNVISITED:
                return REDIRECT + LINK_UNVISITED_NOTIFICATION_LIST_URL;
            case LINK_VISITED:
                return REDIRECT + LINK_VISITED_NOTIFICATION_LIST_URL;
            default:
                return REDIRECT + ALL_NOTIFICATION_LIST_URL;
        }
    }

    @ResponseBody
    @PostMapping(NOTIFICATION_DELETE_URL)
    public ResponseEntity deleteNotification(@Valid @RequestBody NotificationDeleteRequestDto notificationDeleteRequestDto,
                                             Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        notificationService.deleteNotification(notificationDeleteRequestDto);
        return ResponseEntity.ok().build();
    }
}
