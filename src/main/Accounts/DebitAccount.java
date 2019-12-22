package main.Accounts;

import main.Client;
import main.Operation;

public class DebitAccount extends Account {

    public DebitAccount(Client client, AccountRules rules, Integer id, Boolean trust) {
        super(client, rules, id, trust);
    }

    public DebitAccount(Account account) {
        super(account);
    }

    @Override
    public AccountState createSnapshot(Operation nextOperation) {
        return new DebitAccount.AccountState(new DebitAccount(this), nextOperation);
    }
}