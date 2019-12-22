package main.Accounts;

public class AccountRules {

    private final Double yearPercent;
    private final Double commission;
    private final Double strangerLimit;

    public AccountRules(Double yearPercent,
                        Double commission,
                        Double strangerLimit) {
        this.yearPercent   = yearPercent;
        this.commission    = commission;
        this.strangerLimit = strangerLimit;
    }

    public Double getStrangerLimit() {
        return strangerLimit;
    }

    public Double getYearPercent() {
        return yearPercent;
    }

    public Double getCommission() {
        return commission;
    }
}
