package monzo;

interface Ledger {
        // Top Level Operations --------------------------------------------

        // balance returns an int of the most recent total balance at this
        // point in time
        int getBalance();

        // deposit records a deposit in to the ledger for amount. Returns
        // an unique entry ID string to identify the deposit
        String deposit(int amount);

        // withdraw records a withdrawal in to the ledger for amount. Returns
        // an unique entry ID string to identify the withdrawal
        String withdraw(int amount);

        // Additional Operations -------------------------------------------

        // balanceAt returns an int of the total balance at the point (and
        // including) of a particular entry ID. If the specified entry ID
        // does not exist in the ledger, it throws an IllegalArgumentException
        int getBalanceAt(String entryId);

        // Transaction Operations ------------------------------------------

        // begin starts a transaction block. Transactions can be nested
        void begin();

        // commit finishes and writes (commits) all open transactions. If Commit
        // is called without a transaction being started, it throws a
        // RuntimeException
        void commit();

        // rollback finishes the current active transaction block but discards all
        // the changes. If Rollback is called without a transaction being started,
        // it throws a RuntimeException
        void rollback();
}
