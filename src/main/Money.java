package main;

import java.math.BigDecimal;

public class Money {

    private Double cash;

    public Money(Double cash) {
        this.cash = cash;
    }

    public double getCash() {
        return BigDecimal.valueOf(cash).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
    }

    public double getRowCash() {
        return cash;
    }

    public void setCash(Double cash) {
        this.cash = cash;
    }

    @Override
    public String toString() {
        return Double.toString(getCash());
    }
}
