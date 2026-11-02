### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:14:17Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database-backed workflows for library lending, webshop ordering, and warehouse stock synchronization.

#### Purpose
- Defines schemas, operational routines, and reporting for catalog and inventory management, borrowing, customer orders, and supplier-feed stock updates.
- Covers three distinct domains: library catalog and loans, shop products and order lines, and warehouse stock levels.

#### Responsibilities
- **Library lending and reporting:** Manages authors, books, members, branches, copies, loans, audit events, late fees, and lending reports.
- **Shop orders and inventory:** Stores products, customers’ orders, and order lines; adds items while reserving stock and recalculating totals.
- **Warehouse synchronization:** Aggregates supplier-feed quantities into stock levels and logs synchronization results or errors.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, branch, copy inventory, and borrowing records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate checkout and return operations and calculate capped overdue fees.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, member activity, and popularity reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, order records, and stock-aware order-line updates.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — warehouse feed, inventory, synchronization logging, and stock-update procedure.

#### Data flow
- Library seed data populates catalog, members, and loan history; checkout and return routines update loans and inventory, with triggers writing audit records. Reports derive results from lending and catalog data. A migration introduces branch-aware copies and links them to members and loans.
- Shop `add_order_item` locks and checks a product by SKU, inserts or increments an order line, reduces stock, and recomputes the order total.
- Warehouse `dbo.usp_SyncStock` aggregates feed quantities since `@Since`, merges them into `dbo.StockLevel`, counts inserted and updated rows, and logs success or failure.

#### Dependencies
- Library objects connect authors to books and books and members to loans; later migration adds branch and copy relationships.
- Shop `order_item` references `customer_order` and `product`; deleting an order cascades to its line items.
- Warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- Database features include PostgreSQL date and set-generation functions, row locking and SQLSTATE errors in the shop workflow, and SQL Server transactions, `MERGE`, `SYSUTCDATETIME()`, and `ERROR_MESSAGE()` in warehouse synchronization.

#### Cross-cutting
- Constraints and indexes support entity integrity, uniqueness, valid inventory, and common catalog and loan lookups.
- Operational routines validate eligibility, availability, or requested quantities and report errors for invalid operations.
- Warehouse synchronization is transactional; errors trigger rollback when active, are logged, and rethrown.
- Inventory representation varies by domain and workflow: library copy counters are replaced by branch-aware copy records in migration, shop stock is decremented per order line, and warehouse stock is accumulated from supplier feeds.
