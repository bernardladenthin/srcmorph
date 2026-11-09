### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:57:06Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 stored procedure that adds or updates an order line, reserves stock, and updates the order total in a web‑shop.

#### Purpose
- Manage order line creation/updating.
- Reserve product inventory.
- Keep order totals accurate.

#### Responsibilities
- **Order line handling** – insert or merge into `order_item`.
- **Stock reservation** – decrement `product.stock` with row locking.
- **Order total recalculation** – update `customer_order.total_cents` after changes.

#### Key units
- **Tables**  
  - `product` – product catalog, price, stock.  
  - `customer_order` – order header, status, total.  
  - `order_item` – line items linking orders to products.  
- **Procedure**  
  - `add_order_item(p_order_id, p_sku, p_qty)` – core routine.

#### Data flow
1. `add_order_item` receives order ID, SKU, and quantity.  
2. Locks the matching `product` row (`SELECT … FOR UPDATE`).  
3. Validates stock ≥ quantity; signals error if insufficient.  
4. Inserts or updates `order_item` (using `ON DUPLICATE KEY`).  
5. Decrements `product.stock` by quantity.  
6. Recalculates `customer_order.total_cents` as the sum of all line totals for that order.

#### Dependencies
- **Internal** – `product`, `customer_order`, `order_item` tables.  
- **External** – MySQL 8 features: `SELECT … FOR UPDATE`, `ON DUPLICATE KEY`, `SIGNAL`.

#### Cross-cutting
- Row‑level locking on `product` to prevent race conditions.  
- Consistent use of `INT UNSIGNED` for monetary amounts (`price_cents`, `total_cents`).  
- Error handling via `SIGNAL` for insufficient stock.  
- Cascading delete from `customer_order` to `order_item` via FK.
