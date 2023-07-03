package modo.service;

import lombok.RequiredArgsConstructor;
import modo.domain.entity.Users;
import modo.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String usersId) throws UsernameNotFoundException {
        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find Users with UID : " + usersId));

        return new org.springframework.security.core.userdetails.User(
                users.getUsername(),
                users.getPassword(),
                users.getAuthorities()
        );
    }
}
