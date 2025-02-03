package com.user.infrastructure.external.randomuser.dto;

import lombok.Data;

@Data
public class RandomUser {
    private Login login;
    private Name name;
    private String email;
    private String gender;
    private Picture picture;
    private Location location;
}


