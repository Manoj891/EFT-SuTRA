package com.fcgo.eft.sutra.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    @Override
    public AuthenticatedUser getAuthentication() {
        return (AuthenticatedUser) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }
}
