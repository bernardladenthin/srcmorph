### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:58:31Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> The package implements three independent domain systems: a PostgreSQL lending library for books and members, a MySQL web shop catalog with order stock reservation, and a T-SQL warehouse stock synchronization from supplier feeds.

#### Purpose
- Defines and operates a PostgreSQL lending library schema with checkout/return logic, late fees, and reporting views.
- Defines a MySQL web shop schema with product catalog, orders, order items, and a stock-reserving order-item procedure.
- Defines a T-SQL warehouse stored procedure that aggregates supplier feed receipts and upserts stock levels with audit logging.

#### Responsibilities
- Library: core DDL for author, book, member, loan, loan_audit, branch, book_copy; reporting views overdue_loans, member_loan_stats, popular_books; lending routines fn_checkout_book, sp_return_book, fn_late_fee; audit trigger trg_loan_audit; schema migration v2 and transactional seed data.
- Shop: product catalog with SKU uniqueness and stock, customer orders with status enum and running totals, order items with composite keys, and add_order_item procedure for stock reservation and total recalculation.
- Warehouse: nightly aggregation of dbo.SupplierFeed receipts by WarehouseId and Sku, MERGE upsert into dbo.StockLevel, and transactional audit logging into dbo.SyncLog with rollback on failure.

#### Key units
- Library tables: author, book, member, loan, loan_audit, branch, book_copy.
- Library views: overdue_loans, member_loan_stats; materialized view popular_books with unique index idx_popular_books_id.
- Library routines: fn_checkout_book, sp_return_book, fn_late_fee; trigger trg_loan_audit with function trg_loan_audit_fn.
- Shop tables: product, customer_order, order_item; procedure add_order_item.
- Warehouse tables: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog; procedure dbo.usp_SyncStock.

#### Data flow
- Library: seed data populates author, book, member, and loan; fn_checkout_book validates member.is_blocked, inserts a loan, and the audit trigger writes a CHECKOUT row to loan_audit; sp_return_book sets returned_on, restores the copy, and the trigger writes a RETURN row; fn_late_fee computes capped overdue cents from due_on and returned_on; views read loan, member, book, and author to surface overdue loans, per-member stats, and popular books.
- Shop: add_order_item receives p_order_id, p_sku, and p_qty, locks the product row with FOR UPDATE, inserts or updates order_item, decrements product.stock, and recomputes customer_order.total_cents from order item line totals.
- Warehouse: dbo.usp_SyncStock filters dbo.SupplierFeed by ReceivedAt >= @Since, groups by WarehouseId and Sku, MERGE upserts into dbo.StockLevel updating OnHand or inserting new rows, captures insert/update counts via OUTPUT $action, and writes a dbo.SyncLog summary row within a transaction that rolls back and rethrows on error.

#### Dependencies
- Library internal: author, book, member, loan, loan_audit, branch, book_copy; 02_views.sql depends on loan, member, book, author; 03_routines.sql depends on member, book, loan and creates loan_audit plus trg_loan_audit; 04_migration_v2.sql depends on book, loan, member and alters branch and book_copy; 05_seed_data.sql requires author, book, member, and loan. External: PostgreSQL with PL/pgSQL, materialized views, partial indexes, and TIMESTAMPTZ.
- Shop internal: product, customer_order, order_item; constraints uk_product_sku, idx_order_customer, fk_item_order (ON DELETE CASCADE), fk_item_product. External: MySQL 8 / InnoDB.
- Warehouse internal: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog; no declared foreign keys between tables. External: T-SQL, SYSUTCDATETIME, ERROR_MESSAGE, @@TRANCOUNT, transaction control.

#### Cross-cutting
- Library: CHECK constraints on copy counters, loan dates, audit actions, copy status, and author birth year; unique constraints on book.isbn, member.email, branch.name, book_copy.barcode, and popular_books.book_id; partial index idx_loan_open on open loans; ON DELETE CASCADE from member to loan; audit trigger fires on loan INSERT and on returned_on transitioning from null to non-null; seed script wrapped in BEGIN / COMMIT; fn_late_fee is IMMUTABLE.
- Shop: row-level locking via SELECT ... FOR UPDATE on product during stock reservation; error handling using SQLSTATE 45000 for missing or insufficient stock; foreign key cascade delete from customer_order to order_item; unique key enforcement on product.sku.
- Warehouse: single T-SQL file; transaction scope with try/catch rollback and rethrow; UTC timestamps via SYSUTCDATETIME; MERGE pattern with OUTPUT clause into table variable for change counting.
