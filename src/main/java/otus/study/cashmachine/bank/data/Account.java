package otus.study.cashmachine.bank.data;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    public Account(final long id, final BigDecimal amount) {
        this.amount = amount;
        this.id = id;
    }

    private BigDecimal amount;
    private long id;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Account account = (Account) o;
        return id == account.id && amount.compareTo(account.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, id);
    }
}
