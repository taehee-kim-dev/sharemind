package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.FindPasswordProcess;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.dto.request.account.FindPasswordRequestDto;
import portfolio2.dto.request.account.ShowPasswordUpdatePageRequestDto;

@Transactional
@RequiredArgsConstructor
@Service
public class FindPasswordService {

    private final FindPasswordProcess findPasswordProcess;

    public void sendFindPasswordEmail(FindPasswordRequestDto findPasswordRequestDto) {
        findPasswordProcess.sendFindPasswordEmail(findPasswordRequestDto);
    }
}
