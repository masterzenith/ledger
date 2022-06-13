package monzo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class InMemoryLedger implements Ledger {
    // Top Level Operations --------------------------------------------

    private int balance;
    private boolean transactionStart;
    Map<String, Integer> entryBalanceMap = new LinkedHashMap<>();
    // Balance returns an int of the most recent total balance at this
    // point in time
    public int getBalance() {
        return balance;
    }

    // Deposit records a deposit in to the ledger for amount. Returns
    // an unique entry ID string to identify the deposit
    public String deposit(int amount) {
        balance = balance + amount;
        String depositId = generateEntryId();
        entryBalanceMap.put(depositId, balance);
        return depositId;
    }

    // Withdraw records a withdrawal in to the ledger for amount. Returns
    // an unique entry ID string to identify the withdrawal
    public String withdraw(int amount) {
        balance = balance - amount;
        String withdrawId = generateEntryId();
        entryBalanceMap.put(withdrawId, balance);
        return withdrawId;
    }

    // Additional Operations -------------------------------------------

    // BalanceAt returns an int of the total balance at the point (and
    // including) of a particular entry ID. If the specified entry ID
    // does not exist in the ledger, it throws an IllegalArgumentException
    public int getBalanceAt(String entryId) {
        if(entryBalanceMap.containsKey(entryId)) {
            return entryBalanceMap.get(entryId);
        } else {
            throw new IllegalArgumentException("This entry ID doesn't exist: " + entryId);
        }
    }

    // Transaction Operations ------------------------------------------

    // Begin starts a transaction block. Transactions can be nested
    public void begin() {
        transactionStart = true;
    }

    // Commit finishes and writes (commits) all open transactions. If Commit
    // is called without a transaction being started, it throws a
    // RuntimeException
    public void commit() {
        if(transactionStart) {
        } else {
            throw new RuntimeException("Commit operation is failed");
        }
    }

    // Rollback finishes the current active transaction block but discards all
    // the changes. If Rollback is called without a transaction being started,
    // it throws a RuntimeException
    // d1 -> 100 begin() d2-> 100 rollback()
    // d1 -> 100 begin() d2 -> 50 w1-> 10 rollback()
    // interesting keep track of the transaction state and what happens when we roll back
    public void rollback() {
        if(transactionStart) {
            Stack<String> keys = new Stack<>();
            keys.addAll(entryBalanceMap.keySet());
            while (!keys.isEmpty()) {
                keys.pop();
            }
        } else {
            throw new RuntimeException("Rollback operation is failed");
        }
    }

    // generateEntryID is a helper method provided to generate a random ID
    // to use for an entry. You can assume that these are completely unique
    private static String generateEntryId() {
        String uuid = UUID.randomUUID().toString();
        return "entry-" + uuid;
    }
}
