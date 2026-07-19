package com.ledgr.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteAccountForm {

    @NotBlank(message = "enter your password to confirm")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
