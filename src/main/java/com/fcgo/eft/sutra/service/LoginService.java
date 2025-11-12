package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.dto.req.LoginReq;
import com.fcgo.eft.sutra.dto.res.LoginRes;
import jakarta.servlet.http.HttpServletRequest;

public interface LoginService {
    LoginRes login(LoginReq req, HttpServletRequest request);
    void checkValidId(String ip);
    void init();
}
