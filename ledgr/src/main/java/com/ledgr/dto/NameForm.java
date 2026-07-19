package com.ledgr.dto;

import jakarta.validation.constraints.NotBlank;

public class NameForm {

    @NotBlank(message = "name can't be blank")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
