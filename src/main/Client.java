package main;

import main.Accounts.Account;

import java.util.*;
import java.util.function.Consumer;

public class Client {

    private final Bank    bank;
    private final String  name;
    private final String  surname;
    private final String  address;
    private final String  passport;
    private final boolean trust;

    private Integer id;
    private Integer lastAccountId = 0;


    private Map<Integer, Account> accounts = new TreeMap<>();

    private Map<Integer, Log> log = new TreeMap<>();


    public Client(Bank    bank,
                  String  name,
                  String  surname,
                  String  address,
                  String  passport,
                  Integer id,
                  boolean trust) {
        this.bank     = bank;
        this.name     = name;
        this.surname  = surname;
        this.address  = address;
        this.passport = passport;
        this.id       = id;
        this.trust    = trust;
    }

    public void addAccount(Account account) {
        accounts.put(lastAccountId, account);
        log.put(lastAccountId++, new Log());
    }

    public Account findAccount(Integer accountId) {
        return accounts.get(accountId);
    }

    public Integer getLastAccountId() { return lastAccountId; }

    public void addLogOperation(Integer           accountId,
                                Consumer<Account> operation,
                                String            info) {

        Account account = accounts.get(accountId);
        Log     accLog  = log.get(accountId);

        Operation accOperation = new Operation(operation,
                accLog.getLastOperationId(), info, account);

        Account.AccountState state = account.createSnapshot(accOperation);
        accLog.addOperation(state);
    }

    public void rollBack(int     accountId,
                         Integer operationId) {
        Log accLog = log.get(accountId);

        Account.AccountState restoredState = accLog.findOperation(operationId);

        Account restoredAccount;
        if (restoredState != null) {
             restoredAccount = restoredState.getAccount();
        }
        else {
            throw new IllegalArgumentException("Can't find operation with this id");
        }

        accLog.removeOperation(operationId);
        Account.AccountState nextState = accLog.getNextState(operationId++);

        while (nextState != null) {
            nextState.applyNextOperation(restoredAccount);
            nextState = accLog.getNextState(operationId++);
        }

        accounts.put(accountId, restoredAccount);
    }

    public void removeLastOperation(int accountId) {
        Log accLog = log.get(accountId);
        accLog.removeOperation(accLog.getLastOperationId() - 1);
    }

    public Collection<Account.AccountState> getAccountStates(int accountId) {
        Log accLog = log.get(accountId);
        return accLog.getAccountStates();
    }

    public void newDay() {
        for (Account account : accounts.values()) {
            account.newDay();
            addLogOperation(account.getId(), (Account acc) -> acc.newDay(), "new day");
        }
    }

    public Bank getBank() {
        return bank;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public String getPassport() {
        return passport;
    }

    public Integer getId() {
        return id;
    }

    public boolean isTrust() {
        return trust;
    }
}
