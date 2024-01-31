package com.myshop.global.custom;

import com.myshop.domain.User;
import com.myshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Can't find user with this email. -> " + email));

        if(user != null){
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            return  userDetails;
        }

        return null;
    }
}