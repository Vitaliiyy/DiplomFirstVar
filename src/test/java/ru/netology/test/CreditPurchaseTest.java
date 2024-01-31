package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataUtils;
import ru.netology.data.DatabaseHelper;
import ru.netology.page.CreditPurchase;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DatabaseHelper.getOrderCount;

public class CreditPurchaseTest {

    private CreditPurchase buyInCredit;

    String url = System.getProperty("sut.url");

    @BeforeEach
    public void openPage() {
        open(url);
        buyInCredit = new CreditPurchase();
        buyInCredit.buyCredit();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    public void cleanDataBase() {
        DatabaseHelper.cleanDatabase();
    }

    @Test
    @DisplayName("1. покупка тура валидной картой ( APPROVED)")
    public void shouldSuccessfulPurchase() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.buySuccess();
        assertEquals("APPROVED", DatabaseHelper.getCreditStatus());
    }

    @Test
    @DisplayName("2. покупка тура невалидной картой (DECLINED)")
    public void shouldUnsuccessfulPurchase() {
        buyInCredit.setCardNumber(DataUtils.getDeclinedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.buyError();
        assertEquals("DECLINED", DatabaseHelper.getCreditStatus());
    }

    @Test
    @DisplayName("3. Не заполнен номер карты")
    public void shouldErrorEmptyCardNumber() {
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("4. Карта одобрена (APPROVED), не заполнен месяц")
    public void shouldErrorEmptyMonth() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("5. Карта одобрена (APPROVED), не заполнен год")
    public void shouldErrorEmptyYear() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("6. Карта одобрена (APPROVED), не заполнен Владелец")
    public void shouldErrorEmptyCardHolder() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.incorrectFormatHidden();
        buyInCredit.fieldNecessarily();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("7. Карта одобрена (APPROVED), не заполнен код CVC")
    public void shouldErrorEmptyCvc() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarily();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("8. длинный номер карты")
    public void shouldErrorLongCardNumber() {
        buyInCredit.setCardNumber(DataUtils.getCardNumber15Digits());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("9. короткий номер карты")
    public void shouldErrorShortCardNumber() {
        buyInCredit.setCardNumber(DataUtils.getCardNumber10Digits());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("10. Карта одобрена (APPROVED), срок карты истёк")
    public void shouldErrorCardExpired() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buyInCredit.setCardYear(DataUtils.getCurrentYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormatHidden();
        buyInCredit.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("11. Карта одобрена (APPROVED), кирилица в номере карты")
    public void shouldErrorRusExpired() {
        buyInCredit.setCardNumber(DataUtils.getCardNumberRusWords());
        buyInCredit.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buyInCredit.setCardYear(DataUtils.getCurrentYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("12. Карта одобрена (APPROVED), спецсимволы в номере карты")
    public void shouldErrorSymbolsExpired() {
        buyInCredit.setCardNumber(DataUtils.getCardNumberSymbolsWords());
        buyInCredit.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buyInCredit.setCardYear(DataUtils.getCurrentYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("13. Карта одобрена (APPROVED), Латиница в номере карты")
    public void shouldErrorEngExpired() {
        buyInCredit.setCardNumber(DataUtils.getCardNumberEngWords());
        buyInCredit.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buyInCredit.setCardYear(DataUtils.getCurrentYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("14. Карта одобрена (APPROVED), длинное значение месяц")
    public void shouldErrorMonthInvalid() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getNumberFrom13To99());
        buyInCredit.setCardYear(DataUtils.getCurrentYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormatHidden();
        buyInCredit.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("15. Карта одобрена (APPROVED), корокое значение месяц")
    public void shouldErrorMonthIncorrect() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.get1Digit());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("16. Карта одобрена (APPROVED), короткое значение год год")
    public void shouldErrorYearIncorrect() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.get1Digit());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("17. Карта одобрена (APPROVED), превышен срок карты")
    public void shouldErrorDeadlineExceeded() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getYearsAfterEndOfExpiration());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormatHidden();
        buyInCredit.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("18 Карта одобрена (APPROVED), некорректный Владелец")
    public void shouldErrorIncorrectCardHolder() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getIncorrectCardHolder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("19 Карта одобрена (APPROVED), короткое имя Владельца")
    public void shouldErrorShotNameCardHolder() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getShortName());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("20 Карта одобрена (APPROVED), длинное имя Владельца")
    public void shouldErrorLongNameCardHolder() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getLongName());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("21 Карта одобрена (APPROVED), код CVC - нули")
    public void shouldErrorCvcSetNulls() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get000());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("22 Карта одобрена (APPROVED), короткий код CVC - 1 цифра")
    public void shouldErrorCvcSetTwoDigit() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get1Digit());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

}
