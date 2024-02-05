package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import java.time.Duration;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PurchasePage {

    private SelenideElement mainHead = $$("h2").find(exactText("Путешествие дня"));
    private SelenideElement buyButton = $$("button").find(exactText("Купить"));
    private SelenideElement buyHead = $$("h3").find(exactText("Оплата по карте"));
    private SelenideElement cardNumber = $(byText("Номер карты")).parent().$("[class='input__control']");
    private SelenideElement cardMonth = $(byText("Месяц")).parent().$("[class='input__control']");
    private SelenideElement cardYear = $(byText("Год")).parent().$("[class='input__control']");
    private SelenideElement cardOwner = $(byText("Владелец")).parent().$("[class='input__control']");
    private SelenideElement cardCvc = $(byText("CVC/CVV")).parent().$("[class='input__control']");
    private SelenideElement buySuccess = $(byText("Операция одобрена Банком.")).parent().$("[class='notification__content']");
    private SelenideElement buyError = $(byText("Ошибка! Банк отказал в проведении операции.")).parent().$("[class='notification__content']");
    private SelenideElement incorrectFormat = $(byText("Неверный формат"));
    private SelenideElement cardExpirationError = $(byText("Неверно указан срок действия карты"));
    private SelenideElement cardExpired = $(byText("Истёк срок действия карты"));
    private SelenideElement fieldNecessarily = $(byText("Поле обязательно для заполнения"));
    private SelenideElement continueButton = $$("button").find(exactText("Продолжить"));

    public void checkBuyCard() {
        mainHead.shouldBe(visible);
        buyButton.click();
        buyHead.shouldBe(visible);
    }

    public void setCardNumber(String number) {
        cardNumber.setValue(number);
    }

    public void setCardMonth(String month) {
        cardMonth.setValue(month);
    }

    public void setCardYear(String year) {
        cardYear.setValue(year);
    }

    public void setCardholder(String user) {
        cardOwner.setValue(user);
    }

    public void setCardCvv(String cvc) {
        cardCvc.setValue(cvc);
    }

    public void clickContinueButton() {
        continueButton.click();
    }

    public void checkBuySuccess() {
        buySuccess.shouldBe(visible, Duration.ofSeconds(10));
    }

    public void checkBuyError() {
        buyError.shouldBe(visible, Duration.ofSeconds(10));
    }

    public void checkIncorrectFormat() {
        incorrectFormat.shouldBe(visible);
    }

    public void checkIncorrectFormatHidden() {
        incorrectFormat.shouldBe(hidden);
    }

    public void checkCardExpirationError() {
        cardExpirationError.shouldBe(visible);
    }

    public void checkCardExpired() {
        cardExpired.shouldBe(visible);
    }

    public void checkFieldNecessarily() {
        fieldNecessarily.shouldBe(visible);
    }

    public void checkFieldNecessarilyHidden() {
        fieldNecessarily.shouldBe(hidden);
    }

}

