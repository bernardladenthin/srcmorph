### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:46Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL stored procedure that manages order line items, reserves inventory, and keeps the order total accurate.

#### Purpose
- Handle adding or updating a single order line.
- Reserve product stock atomically.
- Recalculate the order’s total amount.

#### Responsibilities
- **Order line management** – insert or update `order_item` rows.
- **Inventory control** – decrement `product.stock` with row‑level locking.
- **Order total calculation** – recompute `customer_order.total_cents` after each change.

#### Key units
- **Tables**
  - `product` – product catalog, price, and stock.
  - `customer_order` – order header with status and total.
  - `order_item` – line items linking orders to products.
- **Procedure**
  - `add_order_item(p_order_id, p_sku, p_qty)` – core routine that performs the above responsibilities.

#### Data flow
1. `add_order_item` receives `order_id`, `sku`, and quantity.
2. SELECT `product_id`, `price_cents` FROM `product` WHERE `sku` = … FOR UPDATE.
3. If stock insufficient, SIGNAL error.
4. INSERT or UPDATE `order_item` (ON DUPLICATE KEY) to add the line.
5. UPDATE `product.stock` = stock – qty.
6. Recalculate `customer_order.total_cents` = Σ(`quantity` × `unit_price_cents`) for that order.

#### Dependencies
- **Internal** – `product`, `customer_order`, `order_item` tables.
- **External** – MySQL 8 features: `SELECT … FOR UPDATE`, `ON DUPLICATE KEY UPDATE`, `SIGNAL`.

#### Cross-cutting
- Row‑level locking on `product` to prevent overselling.
- Consistent use of `UNSIGNED` numeric types for IDs, prices, and quantities.
- Error handling via `SIGNAL` when stock is insufficient.
