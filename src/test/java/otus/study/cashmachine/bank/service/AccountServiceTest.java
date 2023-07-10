package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccountMock() {
        // @TODO test account creation with mock and ArgumentMatcher
        long defaultId = 0;
        BigDecimal amount = BigDecimal.valueOf(1000);
        accountService.createAccount(amount);

        ArgumentMatcher<Account> matcher = argument ->
                argument.getId() == defaultId && amount.equals(argument.getAmount());
        verify(accountDao).saveAccount(argThat(matcher));
    }

    @Test
    void createAccountCaptor() {
        // @TODO test account creation with ArgumentCaptor
        long defaultId = 0;
        BigDecimal amount = BigDecimal.valueOf(1000);
        accountService.createAccount(amount);

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountDao).saveAccount(accountArgumentCaptor.capture());

        Account account = accountArgumentCaptor.getValue();
        assertEquals(defaultId, account.getId());
        assertEquals(amount, account.getAmount());
    }

    @Test
    void putMoney() {
        long accountId = 1;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal addedAmount = BigDecimal.valueOf(200);
        Account savedAccount = new Account(accountId, amount);
        when(accountDao.getAccount(accountId)).thenReturn(savedAccount);

        BigDecimal updatedAmount = accountService.putMoney(accountId, addedAmount);
        BigDecimal expectedAmount = amount.add(addedAmount);
        assertEquals(expectedAmount, updatedAmount);
    }

    @Test
    void getMoneyWithEnoughMoney() {
        long accountId = 1;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal subtractedAmount = BigDecimal.valueOf(100);

        Account savedAccount = new Account(accountId, amount);
        when(accountDao.getAccount(accountId)).thenReturn(savedAccount);

        BigDecimal updatedAmount = accountService.getMoney(accountId, subtractedAmount);
        BigDecimal expectedAmount = amount.subtract(subtractedAmount);
        assertEquals(expectedAmount, updatedAmount);
    }

    @Test
    void getMoneyWithNotEnoughMoney() {
        long accountId = 1;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal subtractedAmount = BigDecimal.valueOf(101);

        Account savedAccount = new Account(accountId, amount);
        when(accountDao.getAccount(accountId)).thenReturn(savedAccount);

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.getMoney(accountId, subtractedAmount));

        String expectedMessage = "Not enough money";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getAccount() {
        long accountId = 1;
        BigDecimal amount = BigDecimal.valueOf(100);
        Account savedAccount = new Account(accountId, amount);
        when(accountDao.getAccount(accountId)).thenReturn(savedAccount);

        Account account = accountService.getAccount(accountId);
        assertEquals(savedAccount, account);
    }

    @Test
    void checkBalance() {
        long accountId = 1;
        BigDecimal amount = BigDecimal.valueOf(100);
        Account savedAccount = new Account(accountId, amount);
        when(accountDao.getAccount(accountId)).thenReturn(savedAccount);

        BigDecimal savedAmount = accountService.checkBalance(accountId);
        assertEquals(amount, savedAmount);
    }
}
