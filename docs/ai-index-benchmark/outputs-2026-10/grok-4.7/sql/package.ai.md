### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:47:37Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
Synthesizing the sql package summary from its library, shop, and warehouse subpackages.> Three independent SQL databases for lending circulation, web-shop checkout, and nightly warehouse stock sync.

#### Purpose
- Groups three separate database packages: a PostgreSQL lending library, a MySQL web-shop catalog and checkout, and a SQL Server warehouse stock synchronisation.
- Each package owns its own schema, mutation routines, and reporting or logging; they do not share tables or call each other.

#### Responsibilities
- Lending library (`library/`): catalog (`author`, `book`, `member`, `loan`), overdue and popularity reporting, checkout/return with late fees and `loan_audit`, a v2 migration from copy counters to `branch`/`book_copy`, and demo seed data.
- Web shop (`shop/`): `product`, `customer_order`, and `order_item` storage, plus `add_order_item` stock reservation and order-total refresh.
- Warehouse sync (`warehouse/`): on-hand `dbo.StockLevel`, inbound `dbo.SupplierFeed`, and `dbo.usp_SyncStock` merging feed quantities into stock with `dbo.SyncLog` success or failure.

#### Key units
- Library: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit` / `trg_loan_audit_fn`, views `overdue_loans`, `member_loan_stats`, `popular_books`; v2 `branch` and `book_copy`.
- Shop: `product` (unique `sku`), `customer_order` status `ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `order_item` PK `(order_id, product_id)`, procedure `add_order_item`.
- Warehouse: `dbo.StockLevel` key `(WarehouseId, Sku)`, `dbo.SupplierFeed`, `dbo.SyncLog`, procedure `dbo.usp_SyncStock` (`@Since`).

#### Data flow
- Library checkout reads `member.is_blocked` and `book.copies_free`, decrements free copies, inserts `loan`, and the trigger writes `loan_audit` `CHECKOUT`; return sets `returned_on` and increments `copies_free`. Seed still uses pre-migration counter columns after v2 drops them.
- Shop: `add_order_item(p_order_id, p_sku, p_qty)` locks `product` (`FOR UPDATE`), inserts or increments `order_item`, decrements `stock`, and sets `customer_order.total_cents` from line sums.
- Warehouse: `@Since` filters `dbo.SupplierFeed`; aggregated `SUM(Quantity)` is `MERGE`d into `dbo.StockLevel`; `$action` counts are logged, or a failed `dbo.SyncLog` row is written and the error rethrown.

#### Dependencies
- No cross-package table or routine dependencies among `library/`, `shop/`, and `warehouse/`.
- Dialects stay separate: PostgreSQL (`PL/pgSQL`, materialized views, partial indexes), MySQL 8 (InnoDB, `utf8mb4`, `SIGNAL`), SQL Server T-SQL (`MERGE`, `THROW`, `DATETIME2`).
- Library FKs link `book`→`author`, `loan`→`book`/`member`; shop FKs link `order_item`→`customer_order` (ON DELETE CASCADE) and `product` (`customer_id` has no customer-table FK). Warehouse matches feed to stock logically only, with no declared foreign keys.

#### Cross-cutting
- Stock is enforced differently in each package: library checks and mutates `copies_free` (v2 moves stock to `book_copy.status` without redefining checkout/return); shop locks and decrements `product.stock`; warehouse adds feed quantity onto `OnHand`.
- Money is integer cents in the library late fee and in the shop (`price_cents`, `unit_price_cents`, `total_cents`); warehouse stores quantity, not price.
- Mutation routines raise on invalid state: library on blocked members, no free copy, or bad return; shop `SQLSTATE '45000'` on unknown SKU or insufficient stock; warehouse catch path logs `ERROR_MESSAGE()` then `THROW`.
