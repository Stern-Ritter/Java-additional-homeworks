package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private CardsDao cardsDao;

    @Spy
    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private MoneyBoxService moneyBoxService;

    private CashMachine cashMachine;

    private CashMachineServiceImpl cashMachineService;

    @BeforeEach
    void setUp() {
        cashMachine = new CashMachine(new MoneyBox());
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }

    @Test
    void getMoney() {
        // @TODO create get money test using spy as mock
        String cardNum = "1111";
        String pinCode = "1011";
        BigDecimal amount = BigDecimal.valueOf(1000);
        List<Integer> expectedNotes = List.of(1, 1, 1, 1);

        doReturn(amount).when(cardService).getMoney(cardNum, pinCode, amount);
        doReturn(expectedNotes).when(moneyBoxService).getMoney(cashMachine.getMoneyBox(), amount.intValue());

        List<Integer> notes = cashMachineService.getMoney(cashMachine, cardNum, pinCode, amount);

        verify(cardService, times(1)).getMoney(cardNum, pinCode, amount);
        verify(cardService, never()).putMoney(anyString(), anyString(), any(BigDecimal.class));
        verify(moneyBoxService, times(1)).getMoney(cashMachine.getMoneyBox(), amount.intValue());
        assertIterableEquals(expectedNotes, notes);
    }

    @Test
    void putMoney() {
        String cardNum = "1111";
        String pinCode = "1011";
        int note100 = 1;
        int note500 = 1;
        int note1000 = 1;
        int note5000 = 1;

        List<Integer> notes = List.of(note5000, note1000, note500, note100);
        BigDecimal expectedAmount = BigDecimal.valueOf(
                note100 * 100 + note500 * 500 + note1000 * 1000 + note5000 * 5000);

        doReturn(BigDecimal.ZERO).when(cardService).getBalance(cardNum, pinCode);

        doReturn(expectedAmount).when(cardService).putMoney(cardNum, pinCode, expectedAmount);

        BigDecimal amount = cashMachineService.putMoney(cashMachine, cardNum, pinCode, notes);

        verify(moneyBoxService, times(1)).putMoney(
                cashMachine.getMoneyBox(), note100, note500, note1000, note5000);
        verify(moneyBoxService, never()).getMoney(any(MoneyBox.class), anyInt());
        verify(cardService, times(1)).putMoney(cardNum, pinCode, expectedAmount);
        assertEquals(expectedAmount, amount);
    }

    @Test
    void checkBalance() {
        String cardNum = "1111";
        String pinCode = "1011";
        BigDecimal expectedAmount = BigDecimal.ONE;
        doReturn(expectedAmount).when(cardService).getBalance(cardNum, pinCode);

        BigDecimal amount = cashMachineService.checkBalance(cashMachine, cardNum, pinCode);

        verify(cardService, times(1)).getBalance(cardNum, pinCode);
        assertEquals(expectedAmount, amount);
    }

    @Test
    void changePin() {
        // @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        String cardNum = "1111";
        String oldPinCode = "1011";
        String newPinCode = "0100";

        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> oldPinCodeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPinCodeCaptor = ArgumentCaptor.forClass(String.class);

        doReturn(true)
                .when(cardService).cnangePin(cardNumCaptor.capture(), oldPinCodeCaptor.capture(), newPinCodeCaptor.capture());

        boolean result = cashMachineService.changePin(cardNum, oldPinCode, newPinCode);

        assertEquals(cardNum, cardNumCaptor.getValue());
        assertEquals(oldPinCode, oldPinCodeCaptor.getValue());
        assertEquals(newPinCode, newPinCodeCaptor.getValue());
        assertTrue(result);
    }

    @Test
    void changePinWithAnswer() {
        // @TODO create change pin test using spy as implementation and mock an thenAnswer
        String cardNum = "1111";
        String oldPinCode = "1011";
        String newPinCode = "0100";

        doAnswer(invocation -> oldPinCode.equals(newPinCode))
                .when(cardService).cnangePin(cardNum, oldPinCode, newPinCode);

        boolean result = cashMachineService.changePin(cardNum, oldPinCode, newPinCode);

        verify(cardService, times(1)).cnangePin(cardNum, oldPinCode, newPinCode);
        assertFalse(result);
    }
}