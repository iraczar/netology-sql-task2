package ru.netology.qa.dto;

import lombok.Value;

@Value
public class TransferRequest {
    String from;
    String to;
    int amount;
}