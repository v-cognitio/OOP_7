package main.Accounts;

import main.Client;
import main.Money;
import main.Operation;

public abstract class Account {

    /** constant info */
    protected Client       client;
    protected AccountRules rules;
    protected Integer      id;
    protected Boolean      trust;

    /** state variables */
    protected Money money        = new Money(0.0);
    protected Money monthPercent = new Money(0.0);

    protected Money dailyStrangerBalance;

    protected int nextMonthCounter = 30;


    /** constructors and etc. */

    public Account(Client       client,
                   AccountRules rules,
                   Integer      id,
                   Boolean      trust) {
        this.client = client;
        this.rules  = rules;
        this.id     = id;
        this.trust  = trust;

        dailyStrangerBalance = new Money(rules.getStrangerLimit());
    }

    public Account(Account account) {
        this.copy(account);
    }

    protected void copy(Account account) {
        this.client = account.client;
        this.rules  = account.rules;
        this.id     = account.id;
        this.trust  = account.trust;

        this.money        = new Money(account.money.getCash());
        this.monthPercent = new Money(account.monthPercent.getCash());

        this.dailyStrangerBalance = new Money(account.dailyStrangerBalance.getCash());

        this.nextMonthCounter = account.nextMonthCounter;
    }


    /** getters, setters, etc. */

    public Money getMoney() { return money; }

    public Client getClient() { return client; }

    public Integer getId() { return id; }


    /** main functionality */

    public void cashOut(Money cash) {
        if (money.getCash() - cash.getCash() > 0 &&
           (trust ||
                dailyStrangerBalance.getCash() - cash.getCash() > 0)) {
            money.setCash(money.getCash() - cash.getCash());
            if (!trust)
                dailyStrangerBalance.setCash(dailyStrangerBalance.getCash() - cash.getCash());
        }
        else {
            throw new IllegalArgumentException("You can't cash out because of lack of money or " +
                    "stranger limit");
        }
    }

    public void topUp(Money cash) {
        money.setCash(money.getRowCash() + cash.getRowCash());
    }


    /** money and percent counting */

    public void newDay() {
        monthPercent.setCash(monthPercent.getRowCash() +
                money.getRowCash() * (rules.getYearPercent() / 365) / 100);
        dailyStrangerBalance.setCash(rules.getStrangerLimit());

        --nextMonthCounter;
        if (nextMonthCounter == 0) {
            endMonth();
        }
    }

    protected void endMonth() {
        money.setCash(money.getRowCash() + monthPercent.getCash());
        monthPercent.setCash(0.0);

        nextMonthCounter = 30;
    }


    /** roll back functionality */

    abstract public AccountState createSnapshot(Operation nextOperation);

    public class AccountState {

        private Account   account;
        private Operation nextOperation;

        public AccountState(Account account, Operation nextOperation) {
            this.account       = account;
            this.nextOperation = nextOperation;
        }

        public Account getAccount() {
            return account;
        }

        public Integer getOperationId() { return nextOperation.getId(); }

        public String getOperationInfo() {
            return "operation id: " + nextOperation.getId() + "\n" +
                   "info: " + nextOperation.getInfo();
        }

        public void applyNextOperation(Account account) {
            nextOperation.apply(account);
        }
    }

    /** language essentials */

    @Override
    public String toString() {
        return "id:      " + id + "\n" +
               "balance: " + money;
    }
}
