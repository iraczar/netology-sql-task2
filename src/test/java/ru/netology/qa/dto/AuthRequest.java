package ru.netology.qa.dto;

import lombok.Value;

@Value
public class AuthRequest {
    String login;
    String password;
}