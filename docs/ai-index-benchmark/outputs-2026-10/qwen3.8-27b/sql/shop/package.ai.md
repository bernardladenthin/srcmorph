### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:47:09Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Web shop order placement: product catalog, customer order headers, order line items, and a stored procedure that atomically adds a line to an order while reserving stock and recalculating the order total.

#### Purpose
- Defines the relational schema (product, customer_order, order_item) and a transactional stored procedure for a web shop's order-placement workflow.

#### Responsibilities
- **Catalog & inventory** — `product` table with SKU, price in cents, and mutable stock count.
- **Order lifecycle** — `customer_order` table tracking status (NEW → PAID → SHIPPED → CANCELLED) and running total.
- **Line-item management** — `order_item` table with composite PK (order_id, product_id) supporting idempotent upserts.
- **Transactional order mutation** — `add_order_item` procedure: lock product row, validate stock, upsert line item, decrement stock, recalculate order total.

#### Key units
- `product` — table; product catalog with `sku` (unique), `price_cents`, `stock`.
- `customer_order` — table; order header with `status` enum, `total_cents`, indexed on `(customer_id, created_at)`.
- `order_item` — table; order line items; composite PK `(order_id, product_id)`; FK cascade to `customer_order`.
- `add_order_item` — stored procedure; parameters `(p_order_id, p_sku, p_qty)`; signals SQLSTATE `45000` on unknown SKU or insufficient stock.

#### Data flow
1. Caller invokes `add_order_item(p_order_id, p_sku, p_qty)`.
2. Procedure acquires `FOR UPDATE` row lock on `product` filtered by `sku` and `stock >= p_qty`.
3. Upserts into `order_item` (`ON DUPLICATE KEY UPDATE quantity = quantity + p_qty`).
4. Decrements `product.stock`.
5. Recalculates `customer_order.total_cents` via subquery `SUM(quantity * unit_price_cents)` over `order_item`.

#### Dependencies
- External: `customer_id` references an external customer table not defined in this package.
- Internal: all three tables and the procedure are self-contained within `orders_mysql.sql`.

#### Cross-cutting
- **Concurrency** — `FOR UPDATE` pessimistic row lock on `product` prevents overselling under concurrent inserts.
- **Idempotency** — `ON DUPLICATE KEY UPDATE` on `order_item` allows repeated calls for the same (order, product) pair without duplicate rows.
- **Error handling** — `SIGNAL SQLSTATE '45000'` for business-rule violations (unknown SKU, insufficient stock).
- **Monetary representation** — all prices stored as integer cents (`price_cents`, `unit_price_cents`, `total_cents`) to avoid floating-point issues.
