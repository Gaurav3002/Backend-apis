package com.backend.investment.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String phone;
    private String password;
    private String withdrawPassword;
    private String referralCode;

}
