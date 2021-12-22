package com.wenmrong.community1.community.controller;

import com.wenmrong.community1.community.dto.AccessTokenDTO;
import com.wenmrong.community1.community.dto.GiteeAccessTokenDTO;
import com.wenmrong.community1.community.dto.GithubUser;
import com.wenmrong.community1.community.model.User;
import com.wenmrong.community1.community.provider.GiteeProvider;
import com.wenmrong.community1.community.provider.GithubProvider;
import com.wenmrong.community1.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private GiteeProvider giteeProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Value("${gitee.grant.type}")
    private String grant_type;
    @Value("${gitee.client.id}")
    private String gitee_clientId;
    @Value("${gitee.client.secret}")
    private String gitee_clientSecret;
    @Value("${gitee.redirect.uri}")
    private String gitee_redirectUri;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO(clientId,clientSecret,code,redirectUri,state);
//        accessTokenDTO.setClient_id(clientId);
//        accessTokenDTO.setClient_secret(clientSecret);
//        accessTokenDTO.setCode(code);
//        accessTokenDTO.setState(state);
//        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if (githubUser != null && githubUser.getId() != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatar_url());
            userService.createOrUpdate(user);
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:/";
        } else {
            log.error("callback return github error {}",githubUser);
            return "redirect:/";
        }
    }

    @GetMapping("/giteeCallback")
    public String giteeCallback(@RequestParam(name = "code") String code,
                           HttpServletResponse response) {
        GiteeAccessTokenDTO giteeAccessTokenDTO
                = new GiteeAccessTokenDTO(grant_type,gitee_clientId,gitee_clientSecret,code,gitee_redirectUri);
//        accessTokenDTO.setClient_id(clientId);
//        accessTokenDTO.setClient_secret(clientSecret);
//        accessTokenDTO.setCode(code);
//        accessTokenDTO.setState(state);
//        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessToken = giteeProvider.getAccessToken(giteeAccessTokenDTO);
        GithubUser githubUser = giteeProvider.getUser(accessToken);
        if (githubUser != null && githubUser.getId() != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatar_url());
            userService.createOrUpdate(user);
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:/";
        } else {
            log.error("callback return gitee error {}",githubUser);
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        request.getSession().removeAttribute("userAccount");
        request.getSession().removeAttribute("userInfo");
        request.getSession().removeAttribute("unreadCount");
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("token") || cookie.getName().equals("history")) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        return "redirect:/";

    }


}
