package otus.study.cashmachine.bank.data;

public class Card {
    long id;

    private Long accountId;
    private String number;
    private String pinCode;

    public Card(final long id, final String number, final Long accountId, final String pinCode) {
        this.id = id;
        this.number = number;
        this.accountId = accountId;
        this.pinCode = pinCode;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(final String pinCode) {
        this.pinCode = pinCode;
    }
}
