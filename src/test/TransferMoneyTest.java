//7. A bank customer should be able to transfer money from one account to another. 

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public class TransferMoneyTest {
        private ArrayList<Account> makeAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();

        Account a1 = new Account("Checking");
        a1.setBalance(100.0);

        Account a2 = new Account("Savings");
        a2.setBalance(200.0);

        accounts.add(a1);
        accounts.add(a2);

        return accounts;
    }

    @Test
    void getNumberOfAccounts_returnsCorrectSize() {
        ArrayList<Account> accounts = makeAccounts();
        assertEquals(2, TransferMoney.getNumberOfAccounts(accounts));
    }

    @Test
    void convertIndexInputToInteger_returnsValidIndex() {
        ArrayList<Account> accounts = makeAccounts();
        int result = TransferMoney.convertIndexInputToInteger("1", accounts, false);
        assertEquals(1, result);
    }

    @Test
    void convertAmountInputToDouble_returnsValidAmount() {
        ArrayList<Account> accounts = makeAccounts();
        double result = TransferMoney.convertAmountInputToDouble("50.0", 0, accounts, false);
        assertEquals(50.0, result);
    }
}
