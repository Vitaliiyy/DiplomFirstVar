package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataUtils;
import ru.netology.data.DatabaseHelper;
import ru.netology.page.PurchasePage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DatabaseHelper.getOrderCount;

public class CheckoutTest {

    private PurchasePage buy;
    String url = System.getProperty("sut.url");

    @BeforeEach
    public void openPage() {
        open(url);
        buy = new PurchasePage();
        buy.buyCard();

    }

    @BeforeAll
    static void setAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDown() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    public void cleanDataBase() {
        DatabaseHelper.cleanDatabase();
    }

    @Test
    @DisplayName("1. покупка тура валидной картой ( APPROVED)")
    public void shouldSuccessfulPurchase() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.buySuccess();
        assertEquals("APPROVED", DatabaseHelper.getPaymentStatus());
    }

    @Test
    @DisplayName("2. покупка тура невалидной картой (DECLINED)")
    public void shouldUnsuccessfulPurchase() {
        buy.setCardNumber(DataUtils.getDeclinedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.buyError();
        assertEquals("DECLINED", DatabaseHelper.getPaymentStatus());
    }

    @Test
    @DisplayName("3. Не заполнен номер карты")
    public void shouldErrorEmptyCardNumber() {
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("4. Карта одобрена (APPROVED), не заполнен месяц")
    public void shouldErrorEmptyMonth() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("5. Карта одобрена (APPROVED), не заполнен год")
    public void shouldErrorEmptyYear() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("6. Карта одобрена (APPROVED), не заполнен Владелец")
    public void shouldErrorEmptyCardHolder() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.incorrectFormatHidden();
        buy.fieldNecessarily();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("7. Карта одобрена (APPROVED), не заполнен код CVC")
    public void shouldErrorEmptyCvc() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("8. длинный номер карты")
    public void shouldErrorLongCardNumber() {
        buy.setCardNumber(DataUtils.getCardNumber15Digits());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("9. короткий номер карты")
    public void shouldErrorShortCardNumber() {
        buy.setCardNumber(DataUtils.getCardNumber10Digits());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("10. Карта одобрена (APPROVED), срок карты истёк")
    public void shouldErrorCardExpired() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buy.setCardYear(DataUtils.getCurrentYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("11. Карта одобрена (APPROVED), кирилица в номере карты")
    public void shouldErrorRusExpired() {
        buy.setCardNumber(DataUtils.getCardNumberRusWords());
        buy.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buy.setCardYear(DataUtils.getCurrentYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("12. Карта одобрена (APPROVED), спецсимволы в номере карты")
    public void shouldErrorSymbolsExpired() {
        buy.setCardNumber(DataUtils.getCardNumberSymbolsWords());
        buy.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buy.setCardYear(DataUtils.getCurrentYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("13. Карта одобрена (APPROVED), Латиница в номере карты")
    public void shouldErrorEngExpired() {
        buy.setCardNumber(DataUtils.getCardNumberEngWords());
        buy.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buy.setCardYear(DataUtils.getCurrentYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("14. Карта одобрена (APPROVED), длинное значение месяц")
    public void shouldErrorMonthInvalid() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getNumberFrom13To99());
        buy.setCardYear(DataUtils.getCurrentYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("15. Карта одобрена (APPROVED), корокое значение месяц")
    public void shouldErrorMonthIncorrect() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.get1Digit());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("16. Карта одобрена (APPROVED), короткое значение год год")
    public void shouldErrorYearIncorrect() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.get1Digit());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("17. Карта одобрена (APPROVED), превышен срок карты")
    public void shouldErrorDeadlineExceeded() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getYearsAfterEndOfExpiration());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("18 Карта одобрена (APPROVED), некорректный Владелец")
    public void shouldErrorIncorrectCardHolder() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getIncorrectCardHolder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("19 Карта одобрена (APPROVED), короткое имя Владельца")
    public void shouldErrorShotNameCardHolder() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getShotName());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("20 Карта одобрена (APPROVED), длинное имя Владельца")
    public void shouldErrorLongNameCardHolder() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getLongName());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("21 Карта одобрена (APPROVED), код CVC - нули")
    public void shouldErrorCvcSetNulls() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get000());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("22 Карта одобрена (APPROVED), короткий код CVC - 1 цифра")
    public void shouldErrorCvcSetTwoDigit() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get1Digit());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

}
