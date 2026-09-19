package ru.netology.qa.dto;

import lombok.Value;

@Value
public class VerificationRequest {
    String login;
    String code;
}