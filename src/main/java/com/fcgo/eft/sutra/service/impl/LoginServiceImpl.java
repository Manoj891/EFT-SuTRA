package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.LoginReq;
import com.fcgo.eft.sutra.dto.res.LoginRes;
import com.fcgo.eft.sutra.entity.oracle.ApplicationUser;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.exception.PermissionDeniedException;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final JwtHelper jwtHelper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository repository;
    private final RemoteIpRepository remoteIpRepository;
    private final List<String> ips = new ArrayList<>();

    @Override
    public void init() {
        ips.add("10.100.193.98");
        ips.add("10.100.193.89");
        ips.add("10.100.193.128");
        ips.add("10.100.193.129");
        ips.add("10.100.193.99");
        ips.add("10.100.100.106");
    }

    @Override
    public void checkValidId(String remoteIp) {
        boolean valid = false;
        for (String ip : ips) {
            if (ip.contains(remoteIp)) {
                valid = true;
                break;
            }
        }
        if (!valid) throw new PermissionDeniedException();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginRes login(LoginReq req, HttpServletRequest request) {
        String remoteIp = request.getRemoteAddr();
        checkValidId(remoteIp);
        ApplicationUser user = repository.findByUsernameOrEmail(req.getUsername(), req.getUsername()).orElseThrow(() -> new CustomException("Invalid credentials!!"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid credentials!!");
        }

        return jwtHelper.generateToken(user);
    }


}
