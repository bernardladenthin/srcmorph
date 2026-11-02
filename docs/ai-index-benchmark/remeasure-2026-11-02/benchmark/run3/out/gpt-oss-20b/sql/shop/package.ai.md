### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:04:04Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 stored procedure that manages order line items, reserves inventory, and keeps the order total accurate.

#### Purpose
- Handle adding or updating a single order line.
- Reserve product stock atomically.
- Recalculate the order’s total amount.

#### Responsibilities
- **Order line management** – insert or update `order_item` rows.
- **Inventory control** – decrement `product.stock` with row‑level locking.
- **Order total calculation** – recompute `customer_order.total_cents` from all line items.

#### Key units
- **Tables**
  - `product` – SKU, price, stock.
  - `customer_order` – order status, total, timestamps.
  - `order_item` – line items linking orders to products.
- **Procedure**
  - `add_order_item(p_order_id, p_sku, p_qty)` – core routine performing the three responsibilities.

#### Data flow
1. `add_order_item` receives `p_order_id`, `p_sku`, `p_qty`.
2. Locks the matching `product` row (`SELECT … FOR UPDATE`).
3. Validates existence and sufficient `stock`; errors if not.
4. Inserts or updates the corresponding `order_item` row.
5. Decrements `product.stock` by `p_qty`.
6. Recalculates `customer_order.total_cents` as the sum of all line totals for that order.

#### Dependencies
- **Internal** – `product`, `customer_order`, `order_item` tables.
- **External** – MySQL 8 features: `SELECT … FOR UPDATE`, `ON DUPLICATE KEY UPDATE`, `ENUM` type.

#### Cross-cutting
- Row‑level locking on `product` ensures stock consistency.
- All writes occur within a single transaction implied by the procedure.
- Error handling via MySQL error codes when stock is insufficient or product not found.
