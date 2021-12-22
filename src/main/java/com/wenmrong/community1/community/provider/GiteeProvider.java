package com.wenmrong.community1.community.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wenmrong.community1.community.dto.GiteeAccessTokenDTO;
import com.wenmrong.community1.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author vinside
 * @date 2021/12/22 2:40 下午
 */
@Component
public class GiteeProvider {

    public String getAccessToken(GiteeAccessTokenDTO giteeAccessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(giteeAccessTokenDTO));
        Request request = new Request.Builder()
                .url("https://gitee.com/oauth/token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(string);
//            System.out.println("string："+ string);
            String token = (String) jsonObject.get("access_token");
            System.out.println("token:"+token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
//                .url("https://gitee.com/api/v5/user")
//                .header("Authorization","token "+accessToken)
                .url("https://gitee.com/api/v5/user?access_token=" + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
        }

        return null;
    }
}
