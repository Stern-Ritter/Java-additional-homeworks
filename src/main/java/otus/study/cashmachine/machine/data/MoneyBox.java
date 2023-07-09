package otus.study.cashmachine.machine.data;

public class MoneyBox {
    int note100;
    int note500;
    int note1000;
    int note5000;

    public MoneyBox() {
        this(1000, 1000, 1000, 1000);
    }

    public MoneyBox(final int note100, final int note500, final int note1000, final int note5000) {
        this.note100 = note100;
        this.note500 = note500;
        this.note1000 = note1000;
        this.note5000 = note5000;
    }

    public int getNote100() {
        return note100;
    }

    public void setNote100(final int note100) {
        this.note100 = note100;
    }

    public int getNote500() {
        return note500;
    }

    public void setNote500(final int note500) {
        this.note500 = note500;
    }

    public int getNote1000() {
        return note1000;
    }

    public void setNote1000(final int note1000) {
        this.note1000 = note1000;
    }

    public int getNote5000() {
        return note5000;
    }

    public void setNote5000(final int note5000) {
        this.note5000 = note5000;
    }
}
