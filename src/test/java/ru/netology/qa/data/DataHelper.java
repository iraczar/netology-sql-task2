package ru.netology.qa.data;

import ru.netology.qa.dto.AuthRequest;
import ru.netology.qa.dto.TransferRequest;
import ru.netology.qa.dto.VerificationRequest;

public class DataHelper {

    public static final String DEMO_LOGIN = "vasya";
    public static final String DEMO_PASSWORD = "qwerty123";
    public static final String CARD_1 = "5559 0000 0000 0001";
    public static final String CARD_2 = "5559 0000 0000 0002";
    public static final String NON_EXISTING_CARD = "9999 9999 9999 9999";

    private DataHelper() {
    }

    public static AuthRequest demoAuthRequest() {
        return new AuthRequest(DEMO_LOGIN, DEMO_PASSWORD);
    }

    public static VerificationRequest verificationRequest(String code) {
        return new VerificationRequest(DEMO_LOGIN, code);
    }

    public static TransferRequest transferRequest(String from, String to, int amount) {
        return new TransferRequest(from, to, amount);
    }
}