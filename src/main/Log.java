package main;

import main.Accounts.Account;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Log {

    private Map<Integer, Account.AccountState> data = new TreeMap<>();
    private Integer lastOperationId = 0;

    public void addOperation(Account.AccountState state) {
        ++lastOperationId;
        data.put(state.getOperationId(), state);
    }

    public void removeOperation(Integer id) {
        data.remove(id);
    }

    public Account.AccountState findOperation(Integer id) {
        return data.get(id);
    }

    public Integer getLastOperationId() { return lastOperationId; }

    public Account.AccountState getNextState(Integer id) {
        Account.AccountState nextState = data.get(++id);
        while (id < lastOperationId) {
            if (nextState == null) {
                nextState = data.get(++id);
            }
            else {
                break;
            }
        }
        return nextState;
    }

    public Collection<Account.AccountState> getAccountStates() {
        return data.values();
    }
}
