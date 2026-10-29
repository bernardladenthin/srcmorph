### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:42:03Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A lending library system managing authors, books, members, and loans with checkout, return, and reporting capabilities.

#### Purpose
- Provides the relational structure, business logic, and reporting interfaces for a lending library database.

#### Responsibilities
- **Core schema**: Defines authors, books, members, and loans with integrity constraints and indexes.
- **Checkout/return logic**: PL/pgSQL functions and triggers that enforce member status, book availability, late fees, and audit trails.
- **Migration**: Adds branch support, redistributes copy tracking, renames columns, and retires legacy per-book counters.
- **Reporting**: Views for overdue loans, member statistics, and popular books.

#### Key units
- **author / book / member / loan** (01_schema.sql): Core relational tables with PK/FK constraints and copy‑availability checks.
- **loan_audit** (03_routines.sql): Audit table tracking CHECKOUT and RETURN actions.
- **fn_checkout_book / sp_return_book / fn_late_fee** (03_routines.sql): PL/pgSQL routines for lending workflow and fee calculation.
- **trg_loan_audit** (03_routines.sql): Trigger that writes audit entries on loan insert or returned_on update.
- **overdue_loans / member_loan_stats / popular_books** (02_views.sql): Reporting views with aggregates and materialized view.
- **branch / book_copy** (04_migration_v2.sql): Migration objects for multi‑branch support and copy‑id tracking.
- **member_since / home_branch_id** (04_migration_v2.sql): Renamed/ added member columns.

#### Data flow
- **Checkout**: `fn_checkout_book` validates `member.is_blocked = FALSE` and `book.copies_free > 0`, inserts a `loan` row, decrements `book.copies_free`, and fires `trg_loan_audit` to record CHECKOUT.
- **Return**: `sp_return_book` sets `loan.returned_on = CURRENT_DATE`, increments `book.copies_free`, and fires the trigger to record RETURN.
- **Late fee**: `fn_late_fee` computes 50 cents per day overdue, capped at 2000 cents, using due_on and returned_on.
- **Migration**: Branch and book_copy tables are populated from legacy book data; loan rows gain a `copy_id` FK; member column `joined_on` is renamed to `member_since` and a `home_branch_id` is added.
- **Reporting**: Views join loan, member, book, and author to surface overdue status, per‑member stats, and books with ≥5 loans in the last year.

#### Dependencies
- **Tables**: author, book, member, loan, loan_audit, branch, book_copy.
- **Functions**: fn_checkout_book, fn_late_fee, sp_return_book.
- **Triggers**: trg_loan_audit.
- **Sequences**: loan_audit_audit_id_seq (implied by BIGSERIAL).
- **External**: PostgreSQL generate_series for migration data population.

#### Cross-cutting
- **Check constraints**: book.copies_free ≤ copies_total; loan.due_on > loaned_on; loan_audit.action IN ('CHECKOUT','RETURN').
- **Error handling**: Routines raise exceptions when member is blocked, no free copy exists, or return is attempted on an already‑returned loan.
- **Trigger logic**: Single trigger handles both INSERT (CHECKOUT) and UPDATE of returned_on (RETURN) via NEW/OLD distinction.
- **Naming conventions**: Consistent use of BIGSERIAL PKs, VARCHAR lengths, and FK naming (idx_*, book_copy, member_since).

#### Purpose
- Defines the relational schema for a web shop, including product inventory, customer orders, and order line items.
- Encapsulates order item addition logic within a MySQL stored procedure that ensures data integrity through stock validation and total recalculation.

#### Responsibilities
- **Schema management**: Creates and maintains tables for `product`, `customer_order`, and `order_item` with appropriate keys and constraints.
- **Order processing**: Implements the `add_order_item` procedure to handle item insertion, stock deduction, and order total updates atomically.
- **Data integrity**: Enforces referential integrity via foreign keys, unique constraints on SKU, and stock level checks before modification.

#### Key units
- **product**: Table storing catalog items with `product_id`, `sku`, `name`, `price_cents`, and `stock`; referenced by order items and the ordering procedure.
- **customer_order**: Table tracking individual orders with `order_id`, `customer_id`, `status`, and `total_cents`; serves as the parent for order items.
- **order_item**: Junction table linking orders and products, storing `quantity` and `unit_price_cents`; its primary key is composite on (`order_id`, `product_id`).
- **add_order_item**: Stored procedure accepting `p_order_id`, `p_sku`, and `p_qty`; responsible for stock validation, item insertion/update, and order total recalculation.

#### Data flow
- Procedure `add_order_item` receives an order ID, SKU, and quantity; it locks the target `product` row, validates sufficient stock, inserts or updates the corresponding `order_item` row, decrements `product.stock`, and recalculates `customer_order.total_cents` by summing `quantity * unit_price_cents` across all items for that order.

#### Dependencies
- **product**: Table accessed via `SELECT ... FOR UPDATE` to read `sku`, `stock`, and `price_cents`; written to decrement `stock`.
- **customer_order**: Table accessed via `UPDATE` to recalculate and set `total_cents`; referenced by the foreign key on `order_item`.
- **order_item**: Table written to via `INSERT ... ON DUPLICATE KEY UPDATE` and read via `SUM` aggregate to compute order totals.

#### Cross-cutting
- Uses MySQL 8 `ENUM` for order `status` with a default of `'NEW'`.
- Employs `ON DELETE CASCADE` on the `order_item` foreign key to `customer_order` to maintain consistency on order deletion.
- Relies on `UNIQUE KEY uk_product_sku` to guarantee SKU uniqueness and enable the duplicate-key logic within the procedure.

#### Purpose
- Merges supplier feed data into the warehouse stock table and records sync results in a log table.

#### Responsibilities
- **Stock merge**: Upserts new and updated stock quantities from the supplier feed into the StockLevel table using a MERGE operation.
- **Sync logging**: Inserts a record into SyncLog capturing the start time, row counts (inserted/updated), and any error messages.
- **Error handling**: Rolls back the transaction and logs errors to SyncLog before re-throwing.

#### Key units
- **dbo.StockLevel** (WarehouseId, Sku): Target table for the merge; stores current on-hand quantities and timestamps.
- **dbo.SupplierFeed** (WarehouseId, Sku, Quantity, ReceivedAt): Source feed table filtered by ReceivedAt to identify changes since the last sync.
- **dbo.SyncLog** (SyncLogId, StartedAt, RowsInserted, RowsUpdated, ErrorMessage): Log table capturing the outcome of each sync execution.
- **StockSync procedure**: The T-SQL routine that orchestrates the merge and logging logic.

#### Data flow
1. Procedure declares start timestamp and a table variable to capture merge actions.
2. Executes MERGE on dbo.StockLevel, grouped by WarehouseId and Sku from dbo.SupplierFeed where ReceivedAt >= @Since.
3. When MATCHED: updates OnHand (adds source Quantity) and sets UpdatedAt.
4. WHEN NOT MATCHED BY TARGET: inserts new StockLevel rows with source WarehouseId, Sku, and OnHand.
5. Outputs $action into a table variable; aggregated counts are inserted into dbo.SyncLog.
6. On success, the transaction commits; on error, it rolls back, logs the error, and re-throws.

#### Dependencies
- dbo.StockLevel
- dbo.SupplierFeed
- dbo.SyncLog

#### Cross-cutting
- Uses a table variable (@Changes) to capture MERGE $action outcomes for subsequent logging.
- Error handling pattern: explicit transaction management with rollback and error logging into dbo.SyncLog before re-throwing.
