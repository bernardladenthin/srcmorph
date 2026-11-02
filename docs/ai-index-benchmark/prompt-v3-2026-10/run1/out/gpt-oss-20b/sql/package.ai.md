### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:56:40Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database schemas and stored procedures that manage catalog, e‑commerce, and warehouse inventory across PostgreSQL, MySQL, and SQL Server.

#### Purpose
- Persist and manipulate library, shop, and warehouse data.
- Provide business logic for checkout, order processing, and stock synchronization.
- Offer reporting and audit capabilities.

#### Responsibilities
- **Library** – catalog, member, loan, and audit management with migration to copy‑based inventory.
- **Shop** – e‑commerce order handling, stock reservation, and total calculation.
- **Warehouse** – nightly supplier feed ingestion, inventory merging, and sync logging.

#### Key units
- **Library**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; functions `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger `trg_loan_audit`; views `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**: tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- **Warehouse**: tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Data flow
1. **Library**: `fn_checkout_book` → loan insert → audit log; `sp_return_book` → loan update → audit log.
2. **Shop**: `add_order_item` locks product, validates stock, updates/creates order item, decrements stock, recalculates order total.
3. **Warehouse**: `usp_SyncStock` aggregates `SupplierFeed`, merges into `StockLevel`, counts changes, logs in `SyncLog`.

#### Dependencies
- Internal: cross‑table FK relationships (`author_id`, `branch_id`, `product_id`, etc.).
- External: PostgreSQL built‑ins (`CURRENT_DATE`, `INTERVAL`), MySQL ENUM, MySQL `FOR UPDATE`, SQL Server `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Indexes and constraints for performance and integrity.

#### Cross‑cutting
- Centralized audit via triggers (library) and log tables (warehouse).
- Transactional integrity: shop uses `FOR UPDATE`; warehouse uses single `TRY…CATCH` transaction.
- Consistent use of unsigned integers for IDs and monetary values in MySQL; explicit status enums in MySQL.
- Migration script in library ensures data preservation when shifting to a copy‑based model.
