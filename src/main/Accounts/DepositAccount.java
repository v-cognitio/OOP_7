package main.Accounts;

import main.Client;
import main.Money;
import main.Operation;

public class DepositAccount extends Account {

    private int     daysTillEnd;
    private boolean isActive = true;

    public DepositAccount(Client       client,
                          AccountRules rules,
                          Integer      id,
                          Boolean      trust,
                          int          activeTime) {
        super(client, rules, id, trust);

        this.daysTillEnd = activeTime;
    }

    public DepositAccount(Account account) {
        super(account);
    }

    @Override
    protected void copy(Account account) {
        if (account instanceof DepositAccount) {
            super.copy(account);
            this.daysTillEnd = ((DepositAccount) account).daysTillEnd;
        }
        else {
            throw new IllegalArgumentException("Deposit account can't copy " + Account.class.getName());
        }
    }

    @Override
    public void cashOut(Money cash) {
        if (isActive) {
            throw new IllegalStateException("Deposit now is active. You can't cash out money");
        }
        super.cashOut(cash);
    }

    @Override
    public void newDay() {
        super.newDay();

        if (isActive) {
            --daysTillEnd;
            if (daysTillEnd == 0) {
                isActive = false;
            }
        }
    }

    @Override
    public AccountState createSnapshot(Operation nextOperation) {
        return new DepositAccount.AccountState(new DebitAccount(this), nextOperation);
    }
}
