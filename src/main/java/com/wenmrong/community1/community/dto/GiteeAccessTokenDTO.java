package com.wenmrong.community1.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author vinside
 * @date 2021/12/22 3:33 下午
 */
@Data
@AllArgsConstructor
public class GiteeAccessTokenDTO {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
}
