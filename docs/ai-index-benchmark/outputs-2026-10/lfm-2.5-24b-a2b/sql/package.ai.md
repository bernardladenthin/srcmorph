### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:58:43Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> This package provides a PostgreSQL lending library, a MySQL e-commerce order system, and a SQL Server warehouse stock synchronization service for managing books, products, and inventory.

#### Purpose
- Provide data structures and operations for a lending library (authors, books, members, loans) plus e-commerce product/order management and warehouse stock synchronization.
- Enable analysis of loan status, member activity, book popularity, and order fulfillment with audit and reporting.

#### Responsibilities
- Library: define core schema (author, book, member, loan, branch, book_copy), create reporting views (overdue_loans, member_loan_stats, popular_books materialized view), implement PL/pgSQL routines (fn_checkout_book, sp_return_book, fn_late_fee, trg_loan_audit_fn) for checkout, return, late fee calculation, and audit logging, migrate schema to v2, and seed data.
- Shop: define product, customer_order, order_item tables with constraints; implement add_order_item stored procedure for stock reservation and order total maintenance.
- Warehouse: implement dbo.usp_SyncStock procedure to merge supplier feed into dbo.StockLevel and log results in dbo.SyncLog.

#### Key units
- Tables: author, book, member, loan, loan_audit, branch, book_copy (library); product, customer_order, order_item (shop); dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog (warehouse).
- Views: overdue_loans, member_loan_stats, popular_books (materialized view), idx_popular_books_id.
- Functions/Procedures/Triggers: fn_checkout_book, sp_return_book, fn_late_fee, trg_loan_audit_fn, add_order_item, dbo.usp_SyncStock.
- Indexes: idx_book_author, idx_loan_member, idx_loan_open, idx_popular_books_id.

#### Data flow
- Library: seed inserts rows into author, book, member, loan; migration v2 adds branch and book_copy, renames member.joined_on to member_since, adds home_branch_id, drops copy counters; checkout/return routines update copies_free, insert/update loan, log audit; views join base tables for reporting.
- Shop: order placement request (order_id, sku, quantity) is read; product stock/price is read; order_item is inserted/updated; product stock decremented; customer_order total recalculated; output updated stock, quantities, totals.
- Warehouse: supplier feed rows read from dbo.SupplierFeed; MERGE aggregated feed into dbo.StockLevel; counts inserted/updated in dbo.SyncLog; output refreshed stock levels and sync log.

#### Dependencies
- Library: base tables author, book, member, loan (01_schema.sql); views depend on loan, member, book, author; routines depend on loan, member, book, loan_audit; migration depends on book, branch, book_copy; seed data depends on all base tables; PL/pgSQL and PostgreSQL runtime.
- Shop: product, customer_order, order_item tables; MySQL 8 syntax (ENUM, AUTO_INCREMENT, DELIMITER).
- Warehouse: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog; system functions SYSUTCDATETIME, IDENTITY, ERROR_MESSAGE, THROW.
- Package-level: integrates library lending, e-commerce order processing, and warehouse stock sync.

#### Cross-cutting
- Constraints: book.copies_free <= book.copies_total, loan.due_on > loaned_on, status enums, author birth_year range.
- Audit trail: loan_audit records CHECKOUT on loan insert and RETURN on loan update.
- Late fee formula: min(max(overdue_days*50,0),2000) cents.
- Foreign keys: author.author_id -> book.author_id, book.book_id -> loan.book_id, member.member_id -> loan.member_id (cascade), loan -> book_copy (v2).
- Transactional: BEGIN/COMMIT/ROLLBACK in warehouse; PL/pgSQL in library.
- Row-level locking: SELECT ... FOR UPDATE in shop.
- Error handling: SQLSTATE '45000' with message 'unknown SKU or not enough stock' in shop; ERROR_MESSAGE/THROW in warehouse.
