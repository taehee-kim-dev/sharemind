package portfolio2.config;

public class UrlAndViewName {

    // HomeAndLogInController.java
    public static final String REDIRECT = "redirect:";

    public static final String HOME_URL = "/";
    public static final String HOME_VIEW_NAME = "index";

    public static final String LOGIN_URL = "/login";
    public static final String LOGIN_VIEW_NAME = "account/login";


    // SignUpController.java
    public static final String SIGN_UP_URL = "/sign-up";
    public static final String SIGN_UP_VIEW_NAME = "account/sign-up";

    public static final String EMAIL_VERIFICATION_REQUEST_VIEW_NAME = "account/email-verification-request";



    // EmailVerificationController.java
    public static final String CHECK_EMAIL_VERIFICATION_LINK_URL = "/check-email-verification-link";

    public static final String EMAIL_VERIFICATION_RESULT_VIEW_NAME = "account/email-verification-result";

    public static final String SEND_EMAIL_VERIFICATION_LINK_URL = "/send-email-verification-link";


    // AccountSettingController.java
    public static final String ACCOUNT_SETTING_ACCOUNT_VIEW_NAME = "/account/setting/account";
}
