### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:39Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> MySQL data model for a web shop that manages product catalog, customer orders, and order-line transactions with stock reservation.

#### Purpose
- Defines the schema for a simple e-commerce shop: products, order headers, and order line items.
- Provides a stored procedure to add an order line atomically while checking stock, updating inventory, and keeping the order total current.

#### Responsibilities
- Catalog and inventory: `product` table with unique SKU, price in cents, and stock count.
- Order management: `customer_order` (status lifecycle, timestamps, total) and `order_item` (line-level quantity and unit price).
- Transaction logic: `add_order_item` procedure encapsulating validation, stock decrement, line upsert, and total recalculation.

#### Key units
- `product` — product catalog; PK `product_id`, UNIQUE `sku`, `price_cents`, `stock`.
- `customer_order` — order header; PK `order_id`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `total_cents`, indexed by `customer_id` + `created_at`.
- `order_item` — line items; composite PK `(order_id, product_id)`, FK cascade delete to `customer_order`, FK to `product`.
- `add_order_item` — stored procedure (IN `p_order_id`, `p_sku`, `p_qty`) that locks the product row, signals `45000` if SKU unknown or stock insufficient, upserts the line, decrements stock, and recomputes the order total.

#### Data flow
- Caller invokes `add_order_item` with order id, SKU, and quantity.
- Procedure selects `product_id`/`price_cents` by SKU `FOR UPDATE`, aborting if no row or insufficient stock.
- Inserts into `order_item` (or increments `quantity` on duplicate key).
- Updates `product.stock` downwards; recomputes `customer_order.total_cents` via SUM over the order's lines.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`.
- Referential integrity via FKs `fk_item_order` (cascade delete) and `fk_item_product`.
- No views, functions, sequences, triggers, or external modules referenced.

#### Cross-cutting
- Monetary values stored as integer cents (`INT UNSIGNED`) throughout.
- Composite primary keys on `order_item`; consistent `_cents` and `_id` naming.
- Single stored procedure uses `FOR UPDATE` row locking and `SIGNAL SQLSTATE '45000'` for business-rule errors.
- MySQL 8 dialect, InnoDB, utf8mb4, DELIMITER usage.
