package com.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class RandomUserResponse {
    private List<RandomUser> results;
}