package ru.netology.qa.api;

import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;
import ru.netology.qa.data.DataHelper;
import ru.netology.qa.helper.RestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferApiTest {

    @Test
    @Description("Успешная авторизация возвращает список карт")
    void shouldReturnCardsAfterSuccessfulAuth() {
        String token = RestHelper.loginAsDemoUser();

        assertEquals(2, RestHelper.getCards(token).size());
    }

    @Test
    @Description("Перевод между своими картами изменяет баланс ровно на сумму перевода")
    void shouldTransferMoneyBetweenOwnCards() {
        String token = RestHelper.loginAsDemoUser();
        int amount = 10;

        int balanceFromBefore = RestHelper.getCardBalance(token, DataHelper.CARD_1);
        int balanceToBefore = RestHelper.getCardBalance(token, DataHelper.CARD_2);

        int statusCode = RestHelper.transferAndGetStatusCode(token,
                DataHelper.transferRequest(DataHelper.CARD_1, DataHelper.CARD_2, amount));
        assertEquals(200, statusCode);

        assertEquals(balanceFromBefore - amount, RestHelper.getCardBalance(token, DataHelper.CARD_1));
        assertEquals(balanceToBefore + amount, RestHelper.getCardBalance(token, DataHelper.CARD_2));
    }

    @Test
    @Description("Перевод на несуществующий номер карты должен отклоняться, баланс карты-источника не должен меняться")
    void shouldNotTransferToNonExistingCard() {
        String token = RestHelper.loginAsDemoUser();

        int balanceBefore = RestHelper.getCardBalance(token, DataHelper.CARD_1);

        int statusCode = RestHelper.transferAndGetStatusCode(token,
                DataHelper.transferRequest(DataHelper.CARD_1, DataHelper.NON_EXISTING_CARD, 1));
        assertEquals(400, statusCode);

        assertEquals(balanceBefore, RestHelper.getCardBalance(token, DataHelper.CARD_1));
    }
}