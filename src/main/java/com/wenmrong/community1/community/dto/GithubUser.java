package com.wenmrong.community1.community.dto;

import lombok.Data;
/*
* github和gitee用户信息字段相同
* */
@Data
public class GithubUser {
    private String name;
    private String id;
    private String bio;
    private String avatar_url;

}
