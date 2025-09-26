package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.LoginReq;
import com.fcgo.eft.sutra.dto.res.LoginRes;
import com.fcgo.eft.sutra.entity.oracle.ApplicationUser;
import com.fcgo.eft.sutra.entity.oracle.RemoteIp;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.repository.oracle.ApplicationUserRepository;
import com.fcgo.eft.sutra.repository.oracle.RemoteIpRepository;
import com.fcgo.eft.sutra.security.JwtHelper;
import com.fcgo.eft.sutra.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final JwtHelper jwtHelper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository repository;
    private final RemoteIpRepository remoteIpRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginRes login(LoginReq req, HttpServletRequest request) {
        ApplicationUser user = repository.findByUsernameOrEmail(req.getUsername(), req.getUsername()).orElseThrow(() -> new CustomException("Invalid credentials!!"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid credentials!!");
        }
        String remoteIp = request.getRemoteAddr();
        RemoteIp ip = remoteIpRepository.findByIdAndUsername(remoteIp, req.getUsername())
                .orElse(RemoteIp.builder().id(UUID.randomUUID().toString()).ip(remoteIp).username(req.getUsername()).build());
        remoteIpRepository.save(ip);
        return jwtHelper.generateToken(user);
    }
}
