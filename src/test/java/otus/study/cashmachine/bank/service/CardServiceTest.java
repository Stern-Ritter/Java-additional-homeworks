package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @Mock
    AccountService accountService;

    @Mock
    CardsDao cardsDao;

    @Spy
    @InjectMocks
    CardServiceImpl cardService;

    @Test
    void testCreateCard() {
        when(cardsDao.createCard("5555", 1L, "0123")).thenReturn(
                new Card(1L, "5555", 1L, "0123"));

        Card newCard = cardService.createCard("5555", 1L, "0123");
        assertNotEquals(0, newCard.getId());
        assertEquals("5555", newCard.getNumber());
        assertEquals(1L, newCard.getAccountId());
        assertEquals("0123", newCard.getPinCode());
    }

    @Test
    void checkBalance() {
        Card card = new Card(1L, "1234", 1L, TestUtil.getHash("0000"));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(accountService.checkBalance(1L)).thenReturn(new BigDecimal(1000));

        BigDecimal sum = cardService.getBalance("1234", "0000");
        assertEquals(0, sum.compareTo(new BigDecimal(1000)));
    }

    @Test
    void getMoney() {
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 100L, TestUtil.getHash("0000")));

        when(accountService.getMoney(idCaptor.capture(), amountCaptor.capture()))
                .thenReturn(BigDecimal.TEN);

        cardService.getMoney("1111", "0000", BigDecimal.ONE);

        verify(accountService, only()).getMoney(anyLong(), any());
        assertEquals(BigDecimal.ONE, amountCaptor.getValue());
        assertEquals(100L, idCaptor.getValue().longValue());
    }

    @Test
    void putMoneyWhenCardExists() {
        long cardId = 1L;
        String cardNumber = "1111";
        long accountId = 1L;
        String pinCode = "1209";
        BigDecimal amount = BigDecimal.valueOf(1000);

        Card card = new Card(cardId, cardNumber, accountId, getHash(pinCode));
        when(cardsDao.getCardByNumber(cardNumber))
                .thenReturn(card);
        when(accountService.putMoney(accountId, amount))
                .thenReturn(amount);

        BigDecimal updatedAmount = cardService.putMoney(cardNumber, pinCode, amount);
        verify(accountService, times(1)).putMoney(accountId, amount);
        assertEquals(amount, updatedAmount);
    }

    @Test
    void putMoneyWhenCardNotFound() {
        when(cardsDao.getCardByNumber(anyString()))
                .thenReturn(null);

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.putMoney(anyString(), anyString(), any(BigDecimal.class)));

        String expectedMessage = "No card found";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void changePinWhenCardExists() {
        long cardId = 1L;
        String cardNumber = "1111";
        long accountId = 1L;
        String pinCode = "1209";
        Card card = new Card(cardId, cardNumber, accountId, getHash(pinCode));

        when(cardsDao.getCardByNumber(cardNumber))
                .thenReturn(card);

        String newPinCode = "1110";
        String hashedNewPinCode = getHash(newPinCode);

        boolean result = cardService.cnangePin(cardNumber, pinCode, newPinCode);
        ArgumentMatcher<Card> argumentMatcher = argument ->
                Objects.equals(cardId, argument.getId())
                        && Objects.equals(cardNumber, argument.getNumber())
                        && Objects.equals(accountId, argument.getAccountId())
                        && Objects.equals(hashedNewPinCode, argument.getPinCode());

        verify(cardsDao, times(1)).saveCard(argThat(argumentMatcher));
        assertTrue(result);
    }

    @Test
    void changePinWhenCardNotFound() {
        when(cardsDao.getCardByNumber(anyString()))
                .thenReturn(null);

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.cnangePin(anyString(), anyString(), anyString()));

        String expectedMessage = exception.getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void checkIncorrectPin() {
        Card card = new Card(1L, "1234", 1L, "0000");
        when(cardsDao.getCardByNumber(eq("1234"))).thenReturn(card);

        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            cardService.getBalance("1234", "0012");
        });
        assertEquals(thrown.getMessage(), "Pincode is incorrect");
    }

    private String getHash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(value.getBytes());
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}