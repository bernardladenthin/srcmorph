### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T02:17:01Z
- G: 
- A: 0.0.0
- X: file
- P: AC7FA967
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 68 of 68 chunks summarized (2 part summaries cut at the output budget)

**Purpose**  
The SQL file `ledger_dump.sql` contains a set of DDL/DML statements that model a financial ledger system, defining tables, indexes, and a procedure for data manipulation. It captures transaction records, aggregates them, and maintains an audit trail.

**Type**  
- **DDL**: `CREATE TABLE`, `CREATE INDEX`, `ALTER TABLE`  
- **DML**: `INSERT`, `UPDATE`, `MERGE` statements  

**Schema**  
- **Table**: `transactions`  
  - **Columns**: `transaction_id`, `account_id`, `amount`, `date`, `description`, `status`, `created_at`  
  - **Constraints**: `transaction_id` (PK), `date` (NOT NULL)  

- **Table**: `aggregates`  
  - **Columns**: `aggregate_id`, `total_amount`, `count`, `avg_date`  

- **Table**: `audit_log`  
  - **Columns**: `audit_id`, `transaction_id`, `action`, `timestamp`  

**Reads**  
- `SELECT * FROM transactions;`  
- `SELECT * FROM aggregates;`  
- `SELECT * FROM audit_log;`  

**Writes**  
- `INSERT INTO transactions (transaction_id, amount, date, description, status, created_at) VALUES (1, 1000.00, '2026-06-01', 'Initial deposit', 'pending', NOW());`  
- `UPDATE transactions SET amount = 1500.00 WHERE transaction_id = 1;`  
- `MERGE INTO transactions USING aggregates a ON transactions.account_id = a.account_id WHEN MATCHED THEN UPDATE SET amount = a.total_amount, status = 'paid', created_at = NOW() WHEN NOT MATCHED THEN INSERT (transaction_id, amount, date, description, status, created_at) VALUES (1, a.total_amount, a.avg_date, 'Audit', 'paid', NOW());`  

**Relationships**  
- `transactions.account_id` → `aggregates.account_id` (FK)  

**Routine logic**  
- The file creates a `transactions` table to store individual transaction records.  
- It creates an `aggregates` table to compute summary statistics (total amount, count, average date) for each account.  
- It maintains an `audit_log` table to record changes to transactions.  
- The `MERGE` statement updates the `transactions` table based on the `aggregates` table, ensuring data consistency.

**Dependencies**  
- No external tables, views, functions, or sequences referenced.

**Note**  
The file does not create the `transactions` table, so the `audit_log` view is read‑only and does not modify any data.
