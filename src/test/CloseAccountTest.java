// A bank customer should be able to close an existing account.(Nicole)

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public class CloseAccountTest {
    private ArrayList<Account> accountList;
    private int initialNumOfAccounts;

    @BeforeEach
    public void testSetup() {
        accountList = new ArrayList<Account>();
        CloseAccount.initializeAccountListRandomly(accountList);
        initialNumOfAccounts = CloseAccount.getNumberOfAccounts(accountList);
    }

    @Test
    public void testNumberOfAccountsAfterValidClosing() {
        CloseAccount.convertIndexInputToInteger("0", accountList, false);
        int finalNumberOfAccounts = CloseAccount.getNumberOfAccounts(accountList);
        assertEquals(initialNumOfAccounts - 1, finalNumberOfAccounts);
    }

    @Test
    public void testNumberOfAccountsAfterOutOfBoundIndex() {
        CloseAccount.convertIndexInputToInteger(String.valueOf(CloseAccount.getNumberOfAccounts(accountList) + 1), accountList, false);
        int finalNumberOfAccounts = CloseAccount.getNumberOfAccounts(accountList);
        assertEquals(initialNumOfAccounts, finalNumberOfAccounts);
    }

    @Test
    public void testNumberOfAccountsAfterInvalidString() {
        CloseAccount.convertIndexInputToInteger("not a valid input", accountList, false);
        int finalNumberOfAccounts = CloseAccount.getNumberOfAccounts(accountList);
        assertEquals(initialNumOfAccounts, finalNumberOfAccounts);
    }

}
