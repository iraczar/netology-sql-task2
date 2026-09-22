package ru.netology.qa.api;

import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;
import ru.netology.qa.data.DataHelper;
import ru.netology.qa.dto.CardDto;
import ru.netology.qa.helper.RestHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferApiTest {

    private int findBalance(List<CardDto> cards, String fullCardNumber) {
        String last4 = fullCardNumber.substring(fullCardNumber.length() - 4);
        return cards.stream()
                .filter(c -> c.getNumber().endsWith(last4))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Card not found: " + fullCardNumber))
                .getBalance();
    }

    @Test
    @Description("Успешная авторизация возвращает список карт")
    void shouldReturnCardsAfterSuccessfulAuth() {
        String token = RestHelper.loginAsDemoUser();
        List<CardDto> cards = RestHelper.getCards(token);
        assertEquals(2, cards.size());
    }

    @Test
    @Description("Перевод между своими картами изменяет баланс ровно на сумму перевода")
    void shouldTransferMoneyBetweenOwnCards() {
        String token = RestHelper.loginAsDemoUser();
        int amount = 10;

        List<CardDto> before = RestHelper.getCards(token);
        int balanceFromBefore = findBalance(before, DataHelper.CARD_1);
        int balanceToBefore = findBalance(before, DataHelper.CARD_2);

        int statusCode = RestHelper.transferAndGetStatusCode(token,
                DataHelper.transferRequest(DataHelper.CARD_1, DataHelper.CARD_2, amount));
        assertEquals(200, statusCode);

        List<CardDto> after = RestHelper.getCards(token);
        int balanceFromAfter = findBalance(after, DataHelper.CARD_1);
        int balanceToAfter = findBalance(after, DataHelper.CARD_2);

        assertEquals(balanceFromBefore - amount, balanceFromAfter);
        assertEquals(balanceToBefore + amount, balanceToAfter);
    }

    @Test
    @Description("Перевод на несуществующий номер карты должен отклоняться, баланс карты-источника не должен меняться")
    void shouldNotTransferToNonExistingCard() {
        String token = RestHelper.loginAsDemoUser();

        List<CardDto> before = RestHelper.getCards(token);
        int balanceBefore = findBalance(before, DataHelper.CARD_1);

        int statusCode = RestHelper.transferAndGetStatusCode(token,
                DataHelper.transferRequest(DataHelper.CARD_1, DataHelper.NON_EXISTING_CARD, 1));
        assertEquals(400, statusCode);

        List<CardDto> after = RestHelper.getCards(token);
        int balanceAfter = findBalance(after, DataHelper.CARD_1);
        assertEquals(balanceBefore, balanceAfter);
    }
}