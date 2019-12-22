package main;

import javafx.util.Pair;
import main.Accounts.Account;
import main.Accounts.AccountRules;
import main.Accounts.DebitAccount;

import java.util.ArrayList;
import java.util.List;

public class Application {


    public static void main(String[] args) {
        AccountRules debitRules = new AccountRules(100.0,0.0,40000.0);
        AccountRules depositRules = new AccountRules(0.0,0.0,40000.0);
        AccountRules creditRules = new AccountRules(0.0,15.0,40000.0);
        List<Pair<Double, Double>> depositPercentRules = new ArrayList<>();
        depositPercentRules.add(new Pair<>(0.0, 0.1));
        depositPercentRules.add(new Pair<>(1000000.0, 4.9));
        depositPercentRules.add(new Pair<>(10000000.0, 5.2));
        Bank bank1 = new Bank(0, debitRules, depositRules, creditRules,
                depositPercentRules,10);

        Client client1 = bank1.createClient("a","b","c","d");
        Account acc1 = bank1.createDebitAccount(client1.getId());

        bank1.topUpAccount(client1.getId(), acc1.getId(), new Money(900.0));
        bank1.topUpAccount(client1.getId(), acc1.getId(), new Money(800.0));
        for (int i = 0; i < 33; ++i) {
            bank1.newDay();
            if (i == 10) {
                bank1.topUpAccount(client1.getId(), acc1.getId(), new Money(1100.0));
            }
        }

        //bank1.cashOutAccount(client1.getId(), acc1.getId(), new Money(1100.0));
        bank1.rollBack(client1.getId(), acc1.getId(), 13);
        bank1.newDay();

        for (Account.AccountState state : bank1.getAccountStates(client1.getId(),acc1.getId())) {
            System.out.println(state.getAccount() + "\n" + state.getOperationInfo() + "\n****");
        }
    }
}
