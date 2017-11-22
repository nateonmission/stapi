package com.cezarykluczynski.stapi.auth.account.api;

import com.cezarykluczynski.stapi.auth.oauth.github.dto.GitHubUserDetailsDTO;
import com.cezarykluczynski.stapi.auth.oauth.session.OAuthSessionHolder;
import com.cezarykluczynski.stapi.model.account.entity.Account;
import com.cezarykluczynski.stapi.model.account.repository.AccountRepository;
import com.cezarykluczynski.stapi.util.exception.StapiRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@SuppressWarnings({"MemberName", "ParameterName"})
public class AccountApi {

	private final AccountRepository accountRepository;

	private final OAuthSessionHolder oAuthSessionHolder;

	public AccountApi(AccountRepository accountRepository, OAuthSessionHolder oAuthSessionHolder) {
		this.accountRepository = accountRepository;
		this.oAuthSessionHolder = oAuthSessionHolder;
	}

	public Account ensureExists(GitHubUserDetailsDTO gitHubUserDetailsDTO) {
		return ensureExists(gitHubUserDetailsDTO, 0);
	}

	private Account ensureExists(GitHubUserDetailsDTO gitHubUserDetailsDTO, int depth) {
		if (depth == 2) {
			throw new StapiRuntimeException("Cannot ensure user is saved locally!");
		}

		Long id = gitHubUserDetailsDTO.getId();
		Optional<Account> accountOptional = accountRepository.findByGitHubUserId(id);
		if (!accountOptional.isPresent()) {
			Account account = new Account();
			account.setName(gitHubUserDetailsDTO.getName());
			if (account.getName() == null) {
				account.setName(gitHubUserDetailsDTO.getLogin());
			}
			account.setEmail(gitHubUserDetailsDTO.getEmail());
			account.setGitHubUserId(id);
			try {
				return accountRepository.save(account);
			} catch (DataIntegrityViolationException e) {
				// in an unlikely event of other thread already creating this user, another run should do it
				return ensureExists(gitHubUserDetailsDTO, depth + 1);
			}
		} else {
			return accountOptional.get();
		}
	}

	@Transactional
	public void remove(Long accountId) {
		accountRepository.delete(accountId);
		oAuthSessionHolder.remove();
	}

}
