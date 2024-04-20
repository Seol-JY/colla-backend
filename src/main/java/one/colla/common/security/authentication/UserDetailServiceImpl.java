package one.colla.common.security.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import one.colla.user.application.UserService;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
	private static final String NOT_FOUND_MESSAGE = "사용자를 찾을 수 없습니다.";
	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return userService.getUserById(Long.parseLong(userId))
			.map(CustomUserDetails::from)
			.orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_MESSAGE));
	}
}
