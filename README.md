# Ledger

A ledger is an ordered record of deposits and withdrawals, typically used to
determine the total amount of money at some point.

In this exercise, we're going to pair on implementing an in-memory ledger. 
We've provided an interface that we'd like you to satisfy. Your goal
is to fill in the implementation!

## Guidelines

For the purpose of this exercise, you will retain the ledger data in-memory, so
you will not need to interact with any external databases/data stores.

You are free to consult language resources and documentation on the internet.
Be sure to talk through your assumptions and don't be afraid to ask questions.

### Top Level Operations

Your ledger will implement some top level operations / functions we've 
specified in an interface. 

- `getBalance()` - Returns the current balance
- `deposit(<amount>)` - Adds amount to the current total balance, returns a unique
  entry ID referencing the deposit
- `withdraw(<amount>)` - Reduces total balance amount from the current balance,
  returns a unique entry ID referencing the withdrawal
  
We've provided a `generateEntryId` function to generate a unique random ID.

There's also one additional function to implement

- `getBalanceAt(<entry-id>)` - Returns the current balance up to and
  including the entry ID. Returns an error if the entry ID does
  not exist in the ledger
  
### Transactions

The ledger allows actions to be performed as part of a transaction, similar to
how a database would conduct transactions.

- `begin()` - Start a new transaction block. Transactions can be nested
- `commit()` - Finish **all** open transactions and write their actions to 
  the ledger
- `rollback()` - Finish **only** the current transaction block without 
  writing any actions captured within that particular transaction block
  to the ledger. An error is returned if `rollback()` is called but there 
  are no open transactions
  
The `getBalance` and `getBalanceAt` functions should include any entries from 
deposits and withdrawals within any open transaction block.
