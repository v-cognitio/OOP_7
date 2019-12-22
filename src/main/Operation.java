package main;

import main.Accounts.Account;

import java.util.function.Consumer;

public class Operation {

    private Consumer<Account> operation;
    private Integer           id;
    private String            info;
    private Account           ownerAccount;

    public Operation(Consumer<Account> operation,
                     Integer           id,
                     String            info,
                     Account           ownerAccount) {
        this.operation    = operation;
        this.id           = id;
        this.info         = info;
        this.ownerAccount = ownerAccount;
    }

    public void apply(Account account) {
        operation.accept(account);
    }

    public Integer getId() { return id; }

    public String getInfo() { return info; }

    public Account getOwnerAccount() { return ownerAccount; }

    @Override
    public String toString() {
        return id + ": " + info;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Operation) {
            return this.id.equals(((Operation) obj).id);
        }
        return false;
    }
}
