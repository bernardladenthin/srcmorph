### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T05:23:10Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi‑module system covering a PostgreSQL library management platform, a MySQL web shop, and a nightly warehouse stock sync.

#### Purpose
- **Library**: Model authors, books, members, loans, branches, and audit trails; provide reporting, transaction handling, migration, and seed data.  
- **Shop**: Define e‑commerce entities (product, order, order item) and a stored procedure to add items while reserving stock and updating totals.  
- **Warehouse**: Nightly bulk import of supplier stock feeds into a canonical `StockLevel` table, logging each run in `SyncLog`.

#### Responsibilities
- **Library**
  - Core data model (authors, books, members, loans, branches, copies, audit).
  - Reporting views (overdue loans, member stats, popular books).
  - Transaction functions (checkout, return, late‑fee calculation).
  - Migration to branch‑centric model with per‑copy tracking.
  - Sample seed data for testing.
- **Shop**
  - Product, order, and order‑item tables.
  - `add_order_item` procedure that checks stock, creates/up‑dates order lines, adjusts product stock, and recomputes order totals.
- **Warehouse**
  - Nightly import of supplier feed rows into `StockLevel`.
  - Aggregation of quantities per SKU per warehouse.
  - Logging of import results in `SyncLog`.

#### Key units
- **Library**
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.
  - Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`.
  - Triggers: `trg_loan_audit_fn`.
  - Migration script: `04_migration_v2.sql`.
  - Seed script: `05_seed_data.sql`.
- **Shop**
  - Tables: `product`, `customer_order`, `order_item`.
  - Procedure: `add_order_item` (stock reservation, order line creation, total recalculation).
- **Warehouse**
  - Tables: `SupplierFeed`, `StockLevel`, `SyncLog`.
  - Procedure: `usp_SyncStock` (merge feed rows, log outcome).

#### Data flow
- **Library Checkout/Return**
  1. `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`, logs audit.  
  2. `sp_return_book` marks loan returned, increments `copies_free`, logs audit.  
  3. `fn_late_fee` calculates overdue penalty.  
  4. Views aggregate data for overdue, member, and popularity reports.  
  5. Migration v2 moves copies to `book_copy`, adds branch dimension, renames `joined_on` → `member_since`, adds `home_branch_id`.
- **Shop Order Placement**
  1. Caller supplies `order_id`, `sku`, `quantity`.  
  2. `add_order_item` reads `product` and `customer_order` under lock.  
  3. If stock insufficient, raises error.  
  4. Inserts/updates `order_item`, reduces `product.stock`.  
  5. Recomputes `customer_order.total_cents`.
- **Warehouse Nightly Sync**
  1. Filter `SupplierFeed` rows for current sync window.  
  2. Aggregate by `WarehouseId` + `Sku`.  
  3. `MERGE` into `StockLevel` (add to `OnHand`, update `UpdatedAt`).  
  4. Count inserts/updates, write to `SyncLog` with start time, row counts, and any error.

#### Dependencies
- **Library**
  - Internal: foreign‑key relationships among core tables.  
  - External: PostgreSQL features (SERIAL/BIGSERIAL, TIMESTAMPTZ, CHECK constraints, DEFAULTs, ON DELETE CASCADE, triggers).  
  - Seed data assumes schema exists before running `05_seed_data.sql`.
- **Shop**
  - Internal: `product`, `customer_order`, `order_item` tables.  
  - External: MySQL 8 (stored procedures, `FOR UPDATE` row locking, `SIGNAL` error handling).
- **Warehouse**
  - Internal: `SupplierFeed`, `StockLevel`, `SyncLog` tables.  
  - External: SQL Server (or compatible) MERGE syntax, `usp_SyncStock` procedure.

#### Cross‑cutting
- **Shared identifiers**: `BIGSERIAL` primary keys across all domains (`author_id`, `book_id`, `member_id`, `loan_id`, `product_id`, `order_id`, `sku`).
- **Audit & logging**: Library uses `loan_audit`; Warehouse uses `SyncLog`. Both capture timestamps and counts of changes.
- **Branch dimension**: Introduced in Library migration v2; all copy‑related operations now reference a `branch`.
- **Configuration constants**: Library late‑fee calculation uses fixed cents‑per‑day values; Shop order totals are stored in `total_cents`.
- **Error handling**: Library functions raise errors via `SIGNAL`; Shop procedure uses `SIGNAL` for insufficient stock; Warehouse logs errors in `SyncLog`.
