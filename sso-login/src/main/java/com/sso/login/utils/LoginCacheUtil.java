package com.sso.login.utils;

import com.sso.login.pojo.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginCacheUtil {
    public static Map<String, User> loginUser = new HashMap<>();
}
