package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.process.profile.ProfileSearchProcess;

@RequiredArgsConstructor
@Transactional
@Service
public class ProfileViewService {

    private final ProfileSearchProcess profileSearchProcess;

    public Account findUser(String userIdToSearch) {
        return profileSearchProcess.searchProfile(userIdToSearch);
    }
}
