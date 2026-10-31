### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:05:12Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Synchronizes warehouse stock from supplier feeds, manages a MySQL web shop catalog and orders, and maintains a PostgreSQL lending library schema with loans, copies, and reporting.

#### Purpose
- Provides three independent domain modules: a PostgreSQL lending library, a MySQL web shop catalog/order system, and a T-SQL warehouse stock synchronization procedure.

#### Responsibilities
- Library: lending library schema, seed data, checkout/return routines, late fee calculation, loan auditing, and reporting views for overdue loans and popular books.
- Shop: product catalog, customer orders, order line items, stock reservation, and order total recalculation.
- Warehouse: nightly supplier feed aggregation, stock level merge, and sync logging with error handling.

#### Key units
- library: author, book, member, loan, branch, book_copy, loan_audit tables; fn_checkout_book, sp_return_book, fn_late_fee routines; trg_loan_audit_fn trigger; overdue_loans, member_loan_stats, popular_books views.
- shop: product, customer_order, order_item tables; add_order_item stored procedure.
- warehouse: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog tables; stock_sync_tsql.sql procedure.

#### Data flow
- Library: seed data populates core tables; migration adds branch and book_copy; fn_checkout_book validates availability and inserts loans with audit; sp_return_book marks returns and restores copies; fn_late_fee computes fees; reporting views aggregate overdue loans, member stats, and popular books.
- Shop: add_order_item locks product stock by SKU, validates availability, upserts order items, decrements stock, and recomputes order totals.
- Warehouse: stock_sync_tsql.sql reads SupplierFeed rows since a cutoff, aggregates by WarehouseId and Sku, MERGEs into StockLevel, counts inserts/updates, and logs results or errors to SyncLog.

#### Dependencies
- Library: PostgreSQL (BIGSERIAL, SMALLSERIAL, PL/pgSQL, generate_series, materialized views, partial indexes).
- Shop: MySQL 8 InnoDB with utf8mb4; foreign keys from order_item to customer_order and product.
- Warehouse: SQL Server (SYSUTCDATETIME, ERROR_MESSAGE, @@TRANCOUNT); no declared foreign keys between SupplierFeed and StockLevel.

#### Cross-cutting
- Library: integrity constraints on copies, due dates, copy status, and audit actions; indexes on author, member, open loans, book copies, and popular books; transactional seed and migration scripts; migration retires per-book copy counters in favor of physical copies.
- Shop: SELECT ... FOR UPDATE row locking for stock reservation; SQLSTATE '45000' errors for missing product or insufficient stock; INSERT ... ON DUPLICATE KEY UPDATE upsert; monetary values in integer cents.
- Warehouse: transactional rollback with error logging and THROW; logical join on WarehouseId and Sku without foreign keys.
