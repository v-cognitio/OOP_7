package main.Accounts;

import main.Client;
import main.Money;
import main.Operation;

public class CreditAccount extends Account {

    private boolean negativeThisMonth = false;

    public CreditAccount(Client client, AccountRules rules, Integer id, Boolean trust) {
        super(client, rules, id, trust);
    }

    public CreditAccount(Account account) {
        super(account);
    }

    @Override
    public void cashOut(Money cash) {
        Money realCash = new Money(cash.getCash());
        if (negativeThisMonth) {
            realCash.setCash(realCash.getRowCash() * rules.getCommission());
        }
        if (trust || dailyStrangerBalance.getCash() - realCash.getCash() > 0) {
            money.setCash(money.getCash() - realCash.getCash());
            if (!trust)
                dailyStrangerBalance.setCash(dailyStrangerBalance.getCash() - realCash.getCash());
        }
        else {
            throw new IllegalArgumentException("You can't cash out because of daily stranger limit");
        }

        if (money.getCash() < 0) {
            negativeThisMonth = true;
        }
    }

    @Override
    protected void endMonth() {
        super.endMonth();

        negativeThisMonth = false;
    }

    @Override
    public AccountState createSnapshot(Operation nextOperation) {
        return new CreditAccount.AccountState(new CreditAccount(this), nextOperation);
    }
}
