### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T23:13:34Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi-domain data infrastructure system providing schema, operational routines, and audit trails for lending library, e-commerce, and warehouse inventory management.

#### Purpose
- Provides domain-specific data infrastructure for operational systems: lending library lifecycle, e-commerce order management, and warehouse inventory synchronization with schema, routines, and audit support.

#### Responsibilities
- Schema design and relational integrity for core entities (authors, books, members, products, stock levels) with CHECK constraints and FK patterns
- Operational routines and transactional stored procedures for checkout/return, order item addition, and inventory sync with MERGE and row locking
- Reporting and analytics views for overdue status, member statistics, book popularity, and order totals
- Migration and seed data population for schema evolution and test data across library and shop domains
- Stock and order lifecycle management including stock decrement, quantity accumulation, and availability enforcement
- Sync logging and audit tracking with transactional boundaries, error rollback, and outcome recording

#### Key units
- author table (library): stores author identity, name, birth year, country with CHECK constraints
- book table (library): manages ISBN, title, author linkage, copies with integrity constraints
- member table (library): tracks member email, name, join date, block status, home_branch_id FK
- loan table (library): records checkout sequences, due dates, return status, cascade deletion references
- overdue_loans view (library): joins loan, member, book to surface active overdue loans with days_overdue computation
- member_loan_stats view (library): aggregates per-member loan totals, open count, last loan date
- popular_books view (library): groups by book_id, title, author_name with unique index and times_loaned count
- product table (shop): stores SKU, price in cents, stock quantity for order item lookups
- customer_order table (shop): tracks order status, total cents, creation timestamp
- order_item table (shop): links orders and products with quantity and unit price, composite primary key
- add_order_item procedure (shop): transactional routine validates stock, inserts items, updates inventory and order totals
- StockLevel table (warehouse): warehouse stock table with PK (WarehouseId, Sku) for inventory tracking
- SupplierFeed table (warehouse): inbound feed source filtered by ReceivedAt >= @Since for incremental sync
- SyncLog table (warehouse): sync outcome log recording rows inserted, updated, and errors
- MERGE procedure (warehouse): nightly sync routine aggregating feed rows and updating StockLevel with change tracking

#### Data flow
- Library checkout: fn_checkout_book validates member eligibility, decrements copies_free, inserts loan, triggers audit log; return: sp_return_book sets returned_on, increments copies_free, triggers audit
- Shop order item: add_order_item procedure validates stock with row locking, inserts item or increments quantity via ON DUPLICATE KEY UPDATE, decrements product stock, recomputes order total cents
- Warehouse sync: MERGE procedure reads SupplierFeed rows where ReceivedAt >= @Since, grouped by WarehouseId/Sku, aggregates quantities into StockLevel UPDATE/INSERT, captures actions, logs outcomes into SyncLog

#### Dependencies
- Internal FK patterns: author.book_id, book.loan_id, loan.member_id, book_copy.book_id/branch_id, member.home_branch_id, order_item.order_id/customer_order, product.SKU lookups
- Cross-file routines: library routines depend on member, book, loan, loan_audit; shop procedure depends on product, customer_order, order_item; warehouse MERGE depends on StockLevel, SupplierFeed, SyncLog
- External: PostgreSQL generate_series for migration, MySQL stored procedure syntax, table variable @Changes, system catalog tables for constraints and sequences
- Parameter dependencies: @Since DATETIME2(0) for warehouse incremental sync; CURRENT_DATE defaults in functions; COALESCE patterns across routines

#### Cross-cutting
- CHECK constraints recurring: birth_year range, copies_free <= copies_total, due_on > loaned_on, status enum values, stock quantity non-negative
- FK constraint pattern across all core tables and reference integrations
- Trigger/audit pattern: trg_loan_audit_fn logs CHECKOUT on INSERT and RETURN on UPDATE of returned_on; similar audit intent in sync logging
- Function pattern: fn_ and sp_ prefixed routines with COALESCE and CURRENT_DATE defaults for business logic
- View pattern: reporting views with GROUP BY, unique indexes, computed columns (days_overdue, times_loaned, order total cents)
- Migration pattern: DDL changes, column renames (joined_on -> member_since), data migrations using generate_series CROSS JOIN
- Status enum usage: book copy statuses ('AVAILABLE','ON_LOAN','LOST'), loan actions ('CHECKOUT','RETURN'), order item accumulation via ON DUPLICATE KEY UPDATE
- Transactional integrity: BEGIN TRANSACTION, COMMIT on success, ROLLBACK on failure; CATCH block error logging and THROW
- Row-level locking: FOR UPDATE on product reads to prevent concurrent stock oversell; SQLSTATE '45000' signals for unknown SKU or insufficient stock
