### ai
- H: 1.0
- C: F12B933D
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:44:03Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A multi-domain package combining a lending library, a web shop, and a warehouse stock synchronisation system.

#### Purpose
- Lending library: manage book copies, member loans, checkouts, returns, and late fees.
- Web shop: manage product catalog, customer orders, order line items, stock reservation, and totals.
- Warehouse: merge supplier feed quantities into warehouse stock levels nightly and log outcomes.

#### Responsibilities
- Library: schema and data modeling for authors, books, members, loans, branches, book copies; reporting views for overdue loans, member loan stats, and popular books; checkout and return operations with late fee calculation and audit trail; schema migration and seed data.
- Shop: product catalog management with SKU uniqueness and stock tracking; order creation and line item management; stock reservation and decrement during order placement; order total recalculation from line item sum.
- Warehouse: merge supplier feed rows received since a given timestamp into warehouse stock levels; update existing stock by adding source quantity; insert new stock levels for unmatched rows; log sync start time, inserted and updated counts, and errors.

#### Key units
- book, member, loan, author, branch, book_copy, loan_audit, overdue_loans, member_loan_stats, popular_books, fn_checkout_book, sp_return_book, fn_late_fee, trg_loan_audit_fn
- product, customer_order, order_item, add_order_item
- dbo.usp_SyncStock, dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog

#### Data flow
- Library: fn_checkout_book checks member.is_blocked, decrements book.copies_free, inserts loan with due_on, returns loan_id; sp_return_book sets loan.returned_on, increments book.copies_free; fn_late_fee calculates days overdue, multiplies by 50, caps at 2000; trg_loan_audit_fn inserts audit row on loan INSERT or returned_on update.
- Shop: order placement reads product stock and price, reserves stock FOR UPDATE, inserts order_item, decrements product stock, recalculates customer_order total_cents.
- Warehouse: supplier feed rows filtered by received timestamp; matched to warehouse stock levels by warehouse and SKU; matched rows update on-hand quantity and updated timestamp; unmatched rows insert new stock levels; merge actions logged into sync log with counts and any error.

#### Dependencies
- Library: PostgreSQL DDL (tables, indexes, views, materialized views, functions, procedures, triggers); internal collaborations: book.author_id -> author.author_id, loan.book_id -> book.book_id, loan.member_id -> member.member_id ON DELETE CASCADE, loan.copy_id -> book_copy.copy_id, member.home_branch_id -> branch.branch_id; external modules: generate_series, current_date, interval.
- Shop: product, customer_order, order_item, add_order_item; MySQL 8 dialect, InnoDB engine, utf8mb4 charset.
- Warehouse: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog; no external dependencies specified.

#### Cross-cutting
- Library: CHECK constraints on birth_year, copies_free <= copies_total, status, action; indexes on book(author_id), loan(member_id), loan(due_on) WHERE returned_on IS NULL, popular_books(book_id); materialized view popular_books with unique book_id; trigger trg_loan_audit_fn on loan INSERT or returned_on update; routine logic for overdue_loans, member_loan_stats, popular_books.
- Shop: ENUM status values NEW, PAID, SHIPPED, CANCELLED; composite primary key on order_item (order_id, product_id); foreign keys with ON DELETE CASCADE on order_item; index on customer_order (customer_id, created_at).
- Warehouse: transactional merge with rollback on error; rethrows error after logging; uses table variable for merge actions.
