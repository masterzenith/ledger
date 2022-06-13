package monzo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit test for Ledger.
 */
public class LedgerTest {
    // TestSimpleDeposit goes through depositing some money and expecting
    // it to be present and correct
    @Test
    public void TestSimpleDeposit() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        String d1 = ledger.deposit(100);
        assertNotEmptyEntryID(d1);
        assertBalanceEqual(100, ledger.getBalance());
    }

    // TestSimpleWithdrawal goes through withdrawing some money and expecting
    // the balance to be reflected appropriately
    @Test
    public void TestSimpleWithdrawal() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        String w1 = ledger.withdraw(100);
        assertNotEmptyEntryID(w1);
        assertBalanceEqual(-100, ledger.getBalance());
    }

    // TestSimpleBalance1 goes through a chain of making a deposit and withdrawal
    // to assert that the flow works correctly
    @Test
    public void TestSimpleBalance1() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        String d1 = ledger.deposit(100);
        assertNotEmptyEntryID(d1);
        assertBalanceEqual(100, ledger.getBalance());

        String w1 = ledger.withdraw(10);
        assertEntryIDsNotEqual(d1, w1);
        assertBalanceEqual(90, ledger.getBalance());
    }

    // TestSimpleBalance2 goes through a chain of making a withdrawal and deposit
    // to assert that the flow works correctly
    @Test
    public void TestSimpleBalance2() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        String w1 = ledger.withdraw(10);
        assertBalanceEqual(-10, ledger.getBalance());

        String d1 = ledger.deposit(100);
        assertEntryIDsNotEqual(d1, w1);
        assertBalanceEqual(90, ledger.getBalance());
    }

    // TestBalanceAt asserts that we are correctly tracking balances between
    // operations correctly, so we can retrieve balances at a point in time
    @Test
    public void TestBalanceAt() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        String d1 = ledger.deposit(100);
        assertBalanceEqual(100, ledger.getBalance());

        int balance = ledger.getBalanceAt(d1);
        assertBalanceEqual(100, balance);

        String w1 = ledger.withdraw(10);
        assertEntryIDsNotEqual(d1, w1);
        assertBalanceEqual(90, ledger.getBalance());

        int balanceAt = ledger.getBalanceAt(d1);
        assertBalanceEqual(100, balanceAt);

        int currentBalance = ledger.getBalanceAt(w1);
        assertBalanceEqual(90, currentBalance);
    }

    // TestBalanceAtInvalidID ensures that a bad transaction ID results in an error
    @Test
    public void TestBalanceAtInvalidID() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        Assertions.assertThrows(IllegalArgumentException.class, () -> ledger.getBalanceAt("BAD_ID"));
    }

    // TestTransactionFlow goes through a simple transaction flow
    @Test
    public void TestTransactionFlow() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        // Start our transaction
        ledger.begin();

        // Make a deposit and a withdrawal
        String d1 = ledger.deposit(100);
        String w1 = ledger.withdraw(10);
        assertBalanceEqual(90, ledger.getBalance());
        assertEntryIDsNotEqual(d1, w1);

        // Commit our transaction
        ledger.commit();

        // Expect all of it to have been written appropriately
        assertBalanceEqual(90, ledger.getBalance());

        int balanceAtD1 = ledger.getBalanceAt(d1);
        assertBalanceEqual(100, balanceAtD1);

        int balanceAtW1 = ledger.getBalanceAt(w1);
        assertBalanceEqual(90, balanceAtW1);
    }

    // TestTransactionRollback tests writing a transaction and then calling
    // rollback, making sure that nothing is committed
    @Test
    public void TestTransactionRollback() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        // Start our transaction
        ledger.begin();

        // Make a deposit and a withdrawal
        String d1 = ledger.deposit(100);
        String w1 = ledger.withdraw(10);
        assertBalanceEqual(90, ledger.getBalance());
        assertEntryIDsNotEqual(d1, w1);

        // Rollback our transaction
        ledger.rollback();

        // Expect none of it to have been written
        assertBalanceEqual(0, ledger.getBalance());
        Assertions.assertThrows(IllegalArgumentException.class, () -> ledger.getBalanceAt(d1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ledger.getBalanceAt(w1));
    }

    // TestTransactionNested tests writing nested transactions with commit
    @Test
    public void TestTransactionNested() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        // Start our transaction, create a deposit
        ledger.begin();
        String d1 = ledger.deposit(100);

        // Create a new transaction
        ledger.begin();
        String w1 = ledger.withdraw(10);
        assertBalanceEqual(90, ledger.getBalance());

        // Commit
        ledger.commit();

        // Expect all our data to be written
        assertBalanceEqual(90, ledger.getBalance());

        int balanceAtD1 = ledger.getBalanceAt(d1);
        assertBalanceEqual(100, balanceAtD1);

        int balanceAtW1 = ledger.getBalanceAt(w1);
        assertBalanceEqual(90, balanceAtW1);
    }

    // TestTransactionNestedRollback tests writing nested transactions with a
    // rollback before committing
    @Test
    public void TestTransactionNestedRollback() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        // Start our transaction, create a deposit
        ledger.begin();
        String d1 = ledger.deposit(100);

        // Create a new transaction but roll it back
        ledger.begin();
        String w1 = ledger.withdraw(10);
        assertBalanceEqual(90, ledger.getBalance());
        ledger.rollback();

        // Expect only d1 to be written
        assertBalanceEqual(100, ledger.getBalance());
        int balanceAtD1 = ledger.getBalanceAt(d1);
        assertBalanceEqual(100, balanceAtD1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> ledger.getBalanceAt(w1));

        // Do a commit, expect only d1 again
        ledger.commit();
        assertBalanceEqual(100, ledger.getBalance());
        balanceAtD1 = ledger.getBalanceAt(d1);
        assertBalanceEqual(100, balanceAtD1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> ledger.getBalanceAt(w1));
    }

    // TestTransactionNestedFullRollback tests writing nested transactions but
    // with all transactions roll backed, we expect nothing to be written
    @Test
    public void TestTransactionNestedFullRollback() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        // Start our transaction, create a deposit
        ledger.begin();
        String d1 = ledger.deposit(100);

        // Create a new transaction
        ledger.begin();
        String w1 = ledger.withdraw(10);

        // Now rollback both our transactions
        ledger.rollback();
        ledger.rollback();

        // We shouldn't be in a transaction, so expect an error
        assertBalanceEqual(0, ledger.getBalance());
        Assertions.assertThrows(RuntimeException.class, () -> ledger.commit());

        // Make sure we did not write d1 or w1
        assertBalanceEqual(0, ledger.getBalance());
        Assertions.assertThrows(IllegalArgumentException.class, () -> ledger.getBalanceAt(d1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ledger.getBalanceAt(w1));
    }

    // TestTransactionBlockInactiveError tests for appropriate errors if COMMIT
    // or ROLLBACK are called outside of a transaction
    @Test
    public void TestTransactionBlockInactiveError() {
        Ledger ledger = new InMemoryLedger();
        assertBalanceEqual(0, ledger.getBalance());

        // Commit outside an active transaction should cause an error
        Assertions.assertThrows(RuntimeException.class, () -> ledger.commit());

        // Rollback outside an active transaction should cause an error
        Assertions.assertThrows(RuntimeException.class, () -> ledger.rollback());
    }

    // assertBalanceEqual takes two int values and expects them to be equal
    public void assertBalanceEqual(int expected, int actual) {
        Assertions.assertEquals(expected, actual, "balance is not what we expected");
    }

    // assertNotEmptyEntryID takes in an entry ID and expects it to be non-empty
    public void assertNotEmptyEntryID(String entryID) {
        Assertions.assertNotNull(entryID, "expected non-empty entry ID string");
    }

    // assertEntryIDsNotEqual takes in two entry IDs and ensures they are different
    // This will also check to make sure they are not empty
    public void assertEntryIDsNotEqual(String a, String b) {
        Assertions.assertNotEquals(a, b, "expected both entry IDs to be different");
    }

}
