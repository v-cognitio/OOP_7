package main;

import javafx.util.Pair;
import main.Accounts.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bank {

    private Integer id;

    private AccountRules debitRules;
    private AccountRules depositRules;
    private AccountRules creditRules;
    private List< Pair<Double, Double> > depositPercentRules; // minimal sum - percent
    private Integer depositPeriod;

    private int lastClientId = 0;

    private List<Client> clients = new ArrayList<>();


    public Bank(Integer      id,
                AccountRules debitRules,
                AccountRules depositRules,
                AccountRules creditRules,
                List< Pair<Double, Double> > depositPercentRules,
                Integer depositPeriod) {
        this.id           = id;
        this.debitRules   = debitRules;
        this.depositRules = depositRules;
        this.creditRules  = creditRules;

        this.depositPercentRules = depositPercentRules;
        this.depositPeriod       = depositPeriod;
    }

    public Integer getId() { return id; }

    public Client createClient(String name,
                               String surname,
                               String address,
                               String passport) {
        boolean trust = true;
        if (address == null || passport == null) {
            trust = false;
        }

        Client newClient = new Client(this, name, surname,
                                        address, passport, lastClientId++, trust);

        clients.add(newClient);
        return newClient;
    }

    public Account createDebitAccount(int clientId) {
        Client client = clients.get(clientId);
        Account newAccount = new DebitAccount(client, debitRules,
                client.getLastAccountId(), client.isTrust());

        client.addAccount(newAccount);
        return newAccount;
    }

    public Account createDepositAccount(int clientId, Money startMoney) {
        Client client = clients.get(clientId);

        Double percent = 0.0;
        for (Pair<Double, Double> rule : depositPercentRules) {
            if (startMoney.getCash() > rule.getKey()) {
                percent = rule.getValue();
            }
            else {
                break;
            }
        }

        AccountRules thisRules = new AccountRules(percent,
                depositRules.getCommission(), depositRules.getStrangerLimit());

        Account newAccount = new DepositAccount(client, thisRules,
                client.getLastAccountId(), client.isTrust(), depositPeriod);
        newAccount.topUp(startMoney);

        client.addAccount(newAccount);
        return newAccount;
    }

    public Account createCreditAccount(int clientId) {
        Client client = clients.get(clientId);
        Account newAccount = new CreditAccount(client, creditRules,
                client.getLastAccountId(), client.isTrust());

        client.addAccount(newAccount);
        return newAccount;
    }

    public void topUpAccount(int clientId, int accountId, Money cash) {
        Client  client  = clients.get(clientId);
        Account account = client.findAccount(accountId);

        double addCash = cash.getCash();
        client.addLogOperation(accountId,
                (Account acc) -> acc.topUp(new Money(addCash)), "add " + cash.getCash());
        account.topUp(cash);
    }

    public void cashOutAccount(int clientId, int accountId, Money cash) {
        Client  client  = clients.get(clientId);
        Account account = client.findAccount(accountId);

        double outCash = cash.getCash();
        client.addLogOperation(accountId,
                (Account acc) -> acc.cashOut(new Money(outCash)), "remove " + cash.getCash());
        try {
            account.cashOut(cash);
        }
        catch (Exception e) {
            client.removeLastOperation(accountId);
            throw e;
        }
    }

    public void rollBack(int clientId, int accountId, int operationId) {
        Client client  = clients.get(clientId);
        client.rollBack(accountId, operationId);
    }

    public Collection<Account.AccountState> getAccountStates(int clientId, int accountId) {
        Client client  = clients.get(clientId);
        return client.getAccountStates(accountId);
    }

    public void newDay() {
        for (Client client : clients) {
            client.newDay();
        }
    }
}

