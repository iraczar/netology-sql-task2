package ru.netology.qa.helper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import ru.netology.qa.data.DataHelper;
import ru.netology.qa.db.SqlHelper;
import ru.netology.qa.dto.AuthRequest;
import ru.netology.qa.dto.CardDto;
import ru.netology.qa.dto.TokenResponse;
import ru.netology.qa.dto.TransferRequest;
import ru.netology.qa.dto.VerificationRequest;

import java.util.List;

import static io.restassured.RestAssured.given;

public class RestHelper {

    static {
        RestAssured.baseURI = "http://localhost:9999";
    }

    private RestHelper() {
    }

    public static void login(AuthRequest request) {
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public static String verify(VerificationRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract().as(TokenResponse.class)
                .getToken();
    }

    public static String loginAsDemoUser() {
        login(DataHelper.demoAuthRequest());
        String code = SqlHelper.getVerificationCode(DataHelper.DEMO_LOGIN);
        return verify(DataHelper.verificationRequest(code));
    }

    public static List<CardDto> getCards(String token) {
        return List.of(given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/cards")
                .then()
                .statusCode(200)
                .extract().as(CardDto[].class));
    }

    public static int getCardBalance(String token, String fullCardNumber) {
        String last4 = fullCardNumber.substring(fullCardNumber.length() - 4);
        return getCards(token).stream()
                .filter(card -> card.getNumber().endsWith(last4))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Card not found: " + fullCardNumber))
                .getBalance();
    }

    public static int transferAndGetStatusCode(String token, TransferRequest request) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/transfer")
                .then()
                .extract().statusCode();
    }
}