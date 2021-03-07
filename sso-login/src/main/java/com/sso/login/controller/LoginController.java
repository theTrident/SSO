package com.sso.login.controller;

import com.sso.login.pojo.User;
import com.sso.login.utils.LoginCacheUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static Set<User> dbUsers;
    static {
        dbUsers = new HashSet<>();
        User user = new User();
        user.setId(3);
        user.setUsername("zhangsan");
        user.setPassword("123");
        dbUsers.add(user);
        //dbUsers.add(new User(1, "zhangsan","12345"));
        //dbUsers.add(new User(2, "lisi", "123456"));
        //dbUsers.add(new User(3, "wangwu","1234567"));
    }

    @PostMapping
    public String doLogin(User user, HttpSession session, HttpServletResponse response) {
        String target = (String) session.getAttribute("target");
        // 模拟从数据库中登录的用户名和密码
        Optional<User> first = dbUsers.stream().filter(dbUser -> dbUser.getUsername().equals(user.getUsername())
                && dbUser.getPassword().equals(user.getPassword())).findFirst();
        // 若用户名、密码正确
        if (first.isPresent()) {
            String token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("TOKEN", token);
            cookie.setDomain("codeshop.com");
            response.addCookie(cookie);
            //保存用户登录信息
            LoginCacheUtil.loginUser.put(token, first.get());

        }else {
            // 登录失败，保存错误提示信息，返回登陆页面
            session.setAttribute("msg", "用户名或密码错误！");
            return "login";
        }
        // 重定向到target地址
        return "redirect:" + target;
    }

    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<User> getUserInfo(String token) {
        // 若token不为空，则根据token拿到用户信息并响应
        if (!StringUtils.isEmpty(token)) {
            User user = LoginCacheUtil.loginUser.get(token);
            return ResponseEntity.ok(user);
        }else {
            // 否则，响应为坏的请求
            return new ResponseEntity<>((User) null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response
    ){

        Cookie[] cookies = request.getCookies();
        for(Cookie cookie:cookies){
            if(cookie!=null && "TOKEN".equals(cookie.getName())){
                //在删除cookie时，只设置maxAge=0不能够从浏览器中删除cookie
                //因为一个Cookie应该属于一个path和domain，所以删除时，Cookie的这两个值也必须设置。

                cookie.setMaxAge(0);
                cookie.setDomain("codeshop.com");
                cookie.setPath("/"); //设置为‘/’ 都可以访问
                response.addCookie(cookie);
            }}
        return "redirect:http://login.codeshop.com:9000/view/login";
    }
}
