package com.lynqo.backend.auth.dto;


import lombok.Data;

@Data
public class GoogleUserInfoResponse {
    String sub;
    String name;
    String given_name;
    String family_name;
    String picture;
    String email;
    Boolean email_verified;
    String hd;
}
