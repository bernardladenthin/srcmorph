### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:03:07Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A lending library system that models authors, books, members, and loans with integrity constraints, checkout logic, and branch management.

#### Purpose
This package provides the full data model and operational logic for a lending library. It manages the relational structure of library entities, enforces business rules around availability and member status, and supports book checkout, returns, late fee calculation, and branch-level copy tracking.

#### Responsibilities
- **Core schema**: Defines authors, books, members, and loans with integrity constraints and indexes.
- **Checkout/return logic**: Functions and triggers that validate member status, manage book availability, and maintain audit trails.
- **Migration and branching**: Adds branch infrastructure, redistributes book copies per branch, and replaces per-book counters with a `book_copy` table.
- **Reporting**: Views and materialized views for overdue tracking, member statistics, and popular books.

#### Key units
- **author table**: Stores author records with identity, name, and country.
- **book table**: Holds book metadata (ISBN, title, author link) and, pre-migration, copy counts and availability constraints.
- **member table**: Member records with contact info, join date, block status, and home branch assignment.
- **loan table**: Transaction records linking members and books, with due dates and return status.
- **loan_audit table**: Audit trail capturing every CHECKOUT and RETURN action.
- **fn_checkout_book function**: Validates member and availability, decrements copies, inserts loan, returns loan_id.
- **sp_return_book procedure**: Marks loan returned, increments book copies, raises error if unknown/already returned.
- **fn_late_fee function**: Calculates capped daily late fees (50c/day, max $20).
- **trg_loan_audit trigger**: Auto-inserts audit records on loan insert or returned_on update.
- **branch table** (post-migration): Branch entities with name and city.
- **book_copy table** (post-migration): Individual copies linked to books and branches, tracking status.
- **overdue_loans view**: Identifies unreturned loans past due date with days_overdue.
- **member_loan_stats view**: Aggregates per-member total and open loan counts.
- **popular_books materialized view**: Books loaned ≥5 times in the last year with author and count.
- **idx_book_copy_book index**: Supports queries on book_copy by book_id.

#### Data flow
1. **Checkout**: `fn_checkout_book` reads member status and book `copies_free`; if valid, it inserts a `loan` row, decrements `book.copies_free` (pre-migration) or links to a `book_copy` row (post-migration), and fires `trg_loan_audit`.
2. **Return**: `sp_return_book` sets `loan.returned_on = CURRENT_DATE`, increments the book's free copy count (or updates the associated `book_copy` status), and fires `trg_loan_audit`.
3. **Late fee**: `fn_late_fee` computes cents from due and returned dates.
4. **Reporting**: Views aggregate loan data across `member`, `book`, and `author` tables to produce overdue lists, member stats, and popularity rankings.

#### Dependencies
- **Tables**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch` (post-migration), `book_copy` (post-migration).
- **Functions**: `fn_checkout_book`, `fn_late_fee`, `sp_return_book`.
- **Triggers**: `trg_loan_audit`.
- **Sequences**: `author_id`, `book_id`, `member_id`, `loan_id`, `loan_audit_audit_id_seq`.
- **Post-migration**: `generate_series` for data migration; FK references between `book_copy`↔`book`, `book_copy`↔`branch`, `loan.copy_id`↔`book_copy.copy_id`, `member.home_branch_id`↔`branch.branch_id`.

#### Cross-cutting
- **Constraint pattern**: `CHECK (copies_free <= copies_total)` pre-migration; replaced by per-row status in `book_copy`.
- **Error handling**: Routines raise exceptions on blocked members, unavailable copies, or invalid return operations.
- **Audit pattern**: All CHECKOUT/RETURN actions flow through `loan_audit` via trigger, ensuring a complete history.
- **Naming evolution**: `member.joined_on` renamed to `member_since`; `book.copies_free/copies_total` removed in favor of `book_copy` status tracking.
- **Index strategy**: FK-indexes on `book.author_id`, `loan.member_id`, `loan.due_on`; post-migration index `idx_book_copy_book` on `book_copy (book_id)`.

#### Purpose
Provides the database schema and stock-enforced order-adding logic for a web shop's order lifecycle.

#### Responsibilities
- Defines the core data model for products, customers, and orders.
- Encapsulates stock validation and order-total maintenance in a single procedure.

#### Key units
- **`add_item_to_order`** (procedure): Accepts a SKU and quantity; validates stock, inserts/updates an order item, reserves stock, and recalculates the order total.
- **`product`** table: Stores catalog data (`sku`, `name`, `price_cents`, `stock`).
- **`customer_order`** table: Tracks orders (`status`, `total_cents`, `customer_id`).
- **`order_item`** table: Links products to orders with quantity and unit price; uses `ON DELETE CASCADE`.

#### Data flow
1. Procedure receives `p_sku` and `p_qty`.
2. Selects `product_id` and `price_cents` from `product` where `sku` matches and `stock >= p_qty`, locking the row with `FOR UPDATE`.
3. If insufficient stock or unknown SKU, raises an error.
4. Inserts a new `order_item` row (or increments quantity via `ON DUPLICATE KEY`).
5. Decrements `product.stock` by `p_qty`.
6. Recalculates `customer_order.total_cents` as `SUM(quantity * unit_price_cents)` for the target order.

#### Dependencies
- `product` table (referenced by SKU and stock check; locked via `FOR UPDATE`).
- `order_item` table (inserted into, updated via `ON DUPLICATE KEY`, read for total recalculation).
- `customer_order` table (updated `total_cents` via aggregate subquery).
- `customer` table (implicit via `customer_order.customer_id` FK, not defined in file).

#### Cross-cutting
- Uses `ON DUPLICATE KEY` pattern for idempotent item insertion.
- Employs `FOR UPDATE` row locking to prevent race conditions on stock decrement.
- Error handling raises 'unknown SKU or not enough stock' when validation fails.

#### Purpose
Merges supplier feed rows received since a given timestamp into the StockLevel table and logs the operation results.

#### Responsibilities
- Ingest and merge supplier feed data into the warehouse stock level table.
- Track merge outcomes (inserts, updates) and capture errors in a dedicated log table.

#### Key units
- **stock_sync_tsql.sql**: T-SQL stored procedure that performs a MERGE operation to upsert stock levels from a supplier feed and logs the results into SyncLog.

#### Data flow
- Reads aggregated supplier feed rows (filtered by `@Since` timestamp, grouped by WarehouseId and Sku).
- Merges into StockLevel: updates OnHand for existing rows, inserts new rows for missing keys.
- Captures merge actions into a table variable, then inserts summary counts and any error message into SyncLog.

#### Dependencies
- `dbo.StockLevel`: target table for the MERGE operation.
- `dbo.SupplierFeed`: source table of feed rows, filtered by `@Since`.
- `dbo.SyncLog`: log table for operation results and errors.
- `SYSUTCDATETIME()`: system function used for timestamps.

#### Cross-cutting
- Uses explicit transaction handling (try/catch with commit/rollback) to ensure atomicity of the merge and log operations.
- Relies on merge-key joins between `StockLevel.WarehouseId` / `StockLevel.Sku` and `SupplierFeed.WarehouseId` / `SupplierFeed.Sku`.
