package org.example.carrent.security;

import org.example.carrent.models.User;
import org.example.carrent.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public AppUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user;

        try {
            user = userService.findByLogin(login);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Nie znaleziono użytkownika: " + login);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getLogin())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}